package com.railse.hiring.workforcemgmt.mapper;

import com.railse.hiring.workforcemgmt.dto.TaskActivityDto;
import com.railse.hiring.workforcemgmt.dto.TaskCommentDto;
import com.railse.hiring.workforcemgmt.dto.TaskManagementDto;
import com.railse.hiring.workforcemgmt.model.TaskActivity;
import com.railse.hiring.workforcemgmt.model.TaskComment;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskManagementMapperImpl implements ITaskManagementMapper {

    @Override
    public TaskManagementDto modelToDto(TaskManagement model) {
        if (model == null) return null;

        TaskManagementDto dto = new TaskManagementDto();
        dto.setId(model.getId());
        dto.setReferenceId(model.getReferenceId());
        dto.setReferenceType(model.getReferenceType());
        dto.setTask(model.getTask());
        dto.setDescription(model.getDescription());
        dto.setStatus(model.getStatus());
        dto.setAssigneeId(model.getAssigneeId());
        dto.setTaskDeadlineTime(model.getTaskDeadlineTime());
        dto.setPriority(model.getPriority());
        dto.setStartDate(model.getStartDate());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());
        dto.setActivities(activityModelListToDtoList(model.getActivities()));
        dto.setComments(commentModelListToDtoList(model.getComments()));

        return dto;
    }

    @Override
    public TaskManagement dtoToModel(TaskManagementDto dto) {
        if (dto == null) return null;

        TaskManagement model = new TaskManagement();
        model.setId(dto.getId());
        model.setReferenceId(dto.getReferenceId());
        model.setReferenceType(dto.getReferenceType());
        model.setTask(dto.getTask());
        model.setDescription(dto.getDescription());
        model.setStatus(dto.getStatus());
        model.setAssigneeId(dto.getAssigneeId());
        model.setTaskDeadlineTime(dto.getTaskDeadlineTime());
        model.setPriority(dto.getPriority());
        model.setStartDate(dto.getStartDate());
        model.setCreatedAt(dto.getCreatedAt());
        model.setUpdatedAt(dto.getUpdatedAt());

        return model;
    }

    @Override
    public List<TaskManagementDto> modelListToDtoList(List<TaskManagement> models) {
        if (models == null) return null;
        return models.stream().map(this::modelToDto).collect(Collectors.toList());
    }

    @Override
    public TaskActivityDto activityModelToDto(TaskActivity activity) {
        if (activity == null) return null;

        TaskActivityDto dto = new TaskActivityDto();
        dto.setId(activity.getId());
        dto.setTaskId(activity.getTaskId());
        dto.setActivityType(activity.getActivityType());
        dto.setDescription(activity.getDescription());
        dto.setUserId(activity.getUserId());
        dto.setOldValue(activity.getOldValue());
        dto.setNewValue(activity.getNewValue());
        dto.setTimestamp(activity.getTimestamp());

        return dto;
    }

    @Override
    public TaskActivity activityDtoToModel(TaskActivityDto dto) {
        if (dto == null) return null;

        TaskActivity activity = new TaskActivity();
        activity.setId(dto.getId());
        activity.setTaskId(dto.getTaskId());
        activity.setActivityType(dto.getActivityType());
        activity.setDescription(dto.getDescription());
        activity.setUserId(dto.getUserId());
        activity.setOldValue(dto.getOldValue());
        activity.setNewValue(dto.getNewValue());
        activity.setTimestamp(dto.getTimestamp());

        return activity;
    }

    @Override
    public List<TaskActivityDto> activityModelListToDtoList(List<TaskActivity> activities) {
        if (activities == null) return null;
        return activities.stream().map(this::activityModelToDto).collect(Collectors.toList());
    }

    @Override
    public TaskCommentDto commentModelToDto(TaskComment comment) {
        if (comment == null) return null;

        TaskCommentDto dto = new TaskCommentDto();
        dto.setId(comment.getId());
        dto.setTaskId(comment.getTaskId());
        dto.setComment(comment.getComment());
        dto.setUserId(comment.getUserId());
        dto.setTimestamp(comment.getTimestamp());

        return dto;
    }

    @Override
    public TaskComment commentDtoToModel(TaskCommentDto dto) {
        if (dto == null) return null;

        TaskComment comment = new TaskComment();
        comment.setId(dto.getId());
        comment.setTaskId(dto.getTaskId());
        comment.setComment(dto.getComment());
        comment.setUserId(dto.getUserId());
        comment.setTimestamp(dto.getTimestamp());

        return comment;
    }

    @Override
    public List<TaskCommentDto> commentModelListToDtoList(List<TaskComment> comments) {
        if (comments == null) return null;
        return comments.stream().map(this::commentModelToDto).collect(Collectors.toList());
    }
}