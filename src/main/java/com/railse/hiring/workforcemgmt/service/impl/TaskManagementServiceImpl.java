package com.railse.hiring.workforcemgmt.service.impl;

import com.railse.hiring.workforcemgmt.common.exception.ResourceNotFoundException;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.TaskActivity;
import com.railse.hiring.workforcemgmt.model.TaskComment;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.*;
import com.railse.hiring.workforcemgmt.repository.TaskActivityRepository;
import com.railse.hiring.workforcemgmt.repository.TaskCommentRepository;
import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {

    private final TaskRepository taskRepository;
    private final TaskActivityRepository activityRepository;
    private final TaskCommentRepository commentRepository;
    private final ITaskManagementMapper taskMapper;

    public TaskManagementServiceImpl(TaskRepository taskRepository,
                                     TaskActivityRepository activityRepository,
                                     TaskCommentRepository commentRepository,
                                     ITaskManagementMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.activityRepository = activityRepository;
        this.commentRepository = commentRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        // FEATURE 3: Load activities and comments for complete history
        task.setActivities(activityRepository.findByTaskIdOrderByTimestamp(id));
        task.setComments(commentRepository.findByTaskIdOrderByTimestamp(id));

        return taskMapper.modelToDto(task);
    }

    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
        List<TaskManagement> createdTasks = new ArrayList<>();

        for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(item.getReferenceId());
            newTask.setReferenceType(item.getReferenceType());
            newTask.setTask(item.getTask());
            newTask.setAssigneeId(item.getAssigneeId());
            newTask.setPriority(item.getPriority() != null ? item.getPriority() : Priority.MEDIUM);
            newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
            newTask.setStartDate(item.getStartDate() != null ? item.getStartDate() : System.currentTimeMillis());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");

            TaskManagement savedTask = taskRepository.save(newTask);

            // FEATURE 3: Log activity
            logActivity(savedTask.getId(), ActivityType.TASK_CREATED,
                    "Task created and assigned to user " + item.getAssigneeId(),
                    1L, null, TaskStatus.ASSIGNED.toString());

            createdTasks.add(savedTask);
        }

        return taskMapper.modelListToDtoList(createdTasks);
    }

    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();

        for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));

            TaskStatus oldStatus = task.getStatus();

            if (item.getTaskStatus() != null) {
                task.setStatus(item.getTaskStatus());

                // FEATURE 3: Log status change activity
                ActivityType activityType = getActivityTypeForStatus(item.getTaskStatus());
                logActivity(task.getId(), activityType,
                        "Task status changed from " + oldStatus + " to " + item.getTaskStatus(),
                        1L, oldStatus.toString(), item.getTaskStatus().toString());
            }

            if (item.getDescription() != null) {
                task.setDescription(item.getDescription());
            }

            updatedTasks.add(taskRepository.save(task));
        }

        return taskMapper.modelListToDtoList(updatedTasks);
    }

    @Override
    public String assignByReference(AssignByReferenceRequest request) {
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(
                request.getReferenceId(), request.getReferenceType());

        for (Task taskType : applicableTasks) {
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());

            // BUG FIX #1: Assign to one and cancel the rest (instead of reassigning ALL)
            if (!tasksOfType.isEmpty()) {
                // Assign the first task to the new assignee
                TaskManagement taskToAssign = tasksOfType.get(0);
                Long oldAssigneeId = taskToAssign.getAssigneeId();
                taskToAssign.setAssigneeId(request.getAssigneeId());
                taskRepository.save(taskToAssign);

                // FEATURE 3: Log reassignment activity
                logActivity(taskToAssign.getId(), ActivityType.TASK_ASSIGNED,
                        "Task reassigned from user " + oldAssigneeId + " to user " + request.getAssigneeId(),
                        1L, oldAssigneeId.toString(), request.getAssigneeId().toString());

                // BUG FIX #1: Cancel all other tasks of the same type
                for (int i = 1; i < tasksOfType.size(); i++) {
                    TaskManagement taskToCancel = tasksOfType.get(i);
                    TaskStatus oldStatus = taskToCancel.getStatus();
                    taskToCancel.setStatus(TaskStatus.CANCELLED);
                    taskRepository.save(taskToCancel);

                    // FEATURE 3: Log cancellation activity
                    logActivity(taskToCancel.getId(), ActivityType.TASK_CANCELLED,
                            "Task cancelled due to reassignment", 1L,
                            oldStatus.toString(), TaskStatus.CANCELLED.toString());
                }
            } else {
                // Create a new task if none exist
                TaskManagement newTask = new TaskManagement();
                newTask.setReferenceId(request.getReferenceId());
                newTask.setReferenceType(request.getReferenceType());
                newTask.setTask(taskType);
                newTask.setAssigneeId(request.getAssigneeId());
                newTask.setStatus(TaskStatus.ASSIGNED);
                newTask.setPriority(Priority.MEDIUM);
                newTask.setStartDate(System.currentTimeMillis());
                newTask.setDescription("Task created via assign-by-reference");

                TaskManagement savedTask = taskRepository.save(newTask);

                // FEATURE 3: Log creation activity
                logActivity(savedTask.getId(), ActivityType.TASK_CREATED,
                        "Task created and assigned via reference assignment", 1L,
                        null, TaskStatus.ASSIGNED.toString());
            }
        }

        return "Tasks assigned successfully for reference " + request.getReferenceId();
    }

    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
        List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

        // BUG FIX #2 + FEATURE 1: Filter out CANCELLED tasks and implement smart date filtering
        List<TaskManagement> filteredTasks = tasks.stream()
                .filter(task -> {
                    // BUG FIX #2: Exclude cancelled tasks
                    if (task.getStatus() == TaskStatus.CANCELLED) {
                        return false;
                    }

                    // FEATURE 1: Smart daily task view
                    // Include tasks that:
                    // 1. Started within the date range AND are active
                    // 2. Started before the range but are still active/assigned (not completed)

                    Long taskStartDate = task.getStartDate() != null ? task.getStartDate() :
                            task.getCreatedAt().toEpochSecond(java.time.ZoneOffset.UTC) * 1000;

                    // Tasks that started within the range
                    boolean startedInRange = taskStartDate >= request.getStartDate() &&
                            taskStartDate <= request.getEndDate();

                    // Tasks that started before the range but are still open
                    boolean startedBeforeButOpen = taskStartDate < request.getStartDate() &&
                            (task.getStatus() == TaskStatus.ASSIGNED || task.getStatus() == TaskStatus.STARTED);

                    return startedInRange || startedBeforeButOpen;
                })
                .collect(Collectors.toList());

        return taskMapper.modelListToDtoList(filteredTasks);
    }

    // NEW FEATURE 2: Update task priority
    @Override
    public TaskManagementDto updateTaskPriority(UpdatePriorityRequest request) {
        TaskManagement task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + request.getTaskId()));

        Priority oldPriority = task.getPriority();
        task.setPriority(request.getPriority());
        TaskManagement updatedTask = taskRepository.save(task);

        // FEATURE 3: Log priority change activity
        logActivity(task.getId(), ActivityType.PRIORITY_CHANGED,
                "Priority changed from " + oldPriority + " to " + request.getPriority(),
                request.getUserId(), oldPriority.toString(), request.getPriority().toString());

        return taskMapper.modelToDto(updatedTask);
    }

    // NEW FEATURE 2: Get tasks by priority
    @Override
    public List<TaskManagementDto> getTasksByPriority(Priority priority) {
        List<TaskManagement> tasks = taskRepository.findByPriority(priority);

        // Filter out cancelled tasks
        List<TaskManagement> activeTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.CANCELLED)
                .collect(Collectors.toList());

        return taskMapper.modelListToDtoList(activeTasks);
    }

    // NEW FEATURE 3: Add comment to task
    @Override
    public TaskCommentDto addComment(AddCommentRequest request) {
        // Verify task exists
        taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + request.getTaskId()));

        TaskComment comment = new TaskComment();
        comment.setTaskId(request.getTaskId());
        comment.setComment(request.getComment());
        comment.setUserId(request.getUserId());

        TaskComment savedComment = commentRepository.save(comment);

        // FEATURE 3: Log comment activity
        logActivity(request.getTaskId(), ActivityType.COMMENT_ADDED,
                "Comment added by user " + request.getUserId(),
                request.getUserId(), null, null);

        return taskMapper.commentModelToDto(savedComment);
    }

    // FEATURE 3: Helper method to log activities
    private void logActivity(Long taskId, ActivityType activityType, String description,
                             Long userId, String oldValue, String newValue) {
        TaskActivity activity = new TaskActivity();
        activity.setTaskId(taskId);
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setUserId(userId);
        activity.setOldValue(oldValue);
        activity.setNewValue(newValue);
        activityRepository.save(activity);
    }

    private ActivityType getActivityTypeForStatus(TaskStatus status) {
        switch (status) {
            case STARTED:
                return ActivityType.TASK_STARTED;
            case COMPLETED:
                return ActivityType.TASK_COMPLETED;
            case CANCELLED:
                return ActivityType.TASK_CANCELLED;
            case ASSIGNED:
                return ActivityType.TASK_ASSIGNED;
            default:
                return ActivityType.TASK_ASSIGNED;
        }
    }
}