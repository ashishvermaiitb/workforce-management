package com.railse.hiring.workforcemgmt.controller;

import com.railse.hiring.workforcemgmt.common.model.response.Response;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-mgmt")
public class TaskManagementController {

    private final TaskManagementService taskManagementService;

    public TaskManagementController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    /**
     * Get a single task by ID - ENHANCED with complete history and comments (Feature 3)
     */
    @GetMapping("/{id}")
    public Response<TaskManagementDto> getTaskById(@PathVariable Long id) {
        return new Response<>(taskManagementService.findTaskById(id));
    }

    /**
     * Create new tasks - ENHANCED with activity logging (Feature 3)
     */
    @PostMapping("/create")
    public Response<List<TaskManagementDto>> createTasks(@RequestBody TaskCreateRequest request) {
        return new Response<>(taskManagementService.createTasks(request));
    }

    /**
     * Update existing tasks - ENHANCED with activity logging (Feature 3)
     */
    @PostMapping("/update")
    public Response<List<TaskManagementDto>> updateTasks(@RequestBody UpdateTaskRequest request) {
        return new Response<>(taskManagementService.updateTasks(request));
    }

    /**
     * Assign tasks by reference - BUG FIX #1: Now properly cancels duplicates instead of reassigning all
     */
    @PostMapping("/assign-by-ref")
    public Response<String> assignByReference(@RequestBody AssignByReferenceRequest request) {
        return new Response<>(taskManagementService.assignByReference(request));
    }

    /**
     * Fetch tasks by date range - BUG FIX #2 + FEATURE #1:
     * - Excludes cancelled tasks (Bug Fix #2)
     * - Smart date filtering: tasks in range + ongoing from before range (Feature #1)
     */
    @PostMapping("/fetch-by-date/v2")
    public Response<List<TaskManagementDto>> fetchByDate(@RequestBody TaskFetchByDateRequest request) {
        return new Response<>(taskManagementService.fetchTasksByDate(request));
    }

    // ===============================
    // NEW FEATURE ENDPOINTS
    // ===============================

    /**
     * FEATURE #2: Update task priority with activity logging
     */
    @PutMapping("/priority")
    public Response<TaskManagementDto> updateTaskPriority(@RequestBody UpdatePriorityRequest request) {
        return new Response<>(taskManagementService.updateTaskPriority(request));
    }

    /**
     * FEATURE #2: Get all tasks of a specific priority (excluding cancelled)
     */
    @GetMapping("/priority/{priority}")
    public Response<List<TaskManagementDto>> getTasksByPriority(@PathVariable Priority priority) {
        return new Response<>(taskManagementService.getTasksByPriority(priority));
    }

    /**
     * FEATURE #3: Add comment to a task with activity logging
     */
    @PostMapping("/comment")
    public Response<TaskCommentDto> addComment(@RequestBody AddCommentRequest request) {
        return new Response<>(taskManagementService.addComment(request));
    }
}