package com.railse.hiring.workforcemgmt.mapper;

import com.railse.hiring.workforcemgmt.dto.TaskActivityDto;
import com.railse.hiring.workforcemgmt.dto.TaskCommentDto;
import com.railse.hiring.workforcemgmt.dto.TaskManagementDto;
import com.railse.hiring.workforcemgmt.model.TaskActivity;
import com.railse.hiring.workforcemgmt.model.TaskComment;
import com.railse.hiring.workforcemgmt.model.TaskManagement;

import java.util.List;

public interface ITaskManagementMapper {
    TaskManagementDto modelToDto(TaskManagement model);
    TaskManagement dtoToModel(TaskManagementDto dto);
    List<TaskManagementDto> modelListToDtoList(List<TaskManagement> models);

    TaskActivityDto activityModelToDto(TaskActivity activity);
    TaskActivity activityDtoToModel(TaskActivityDto dto);
    List<TaskActivityDto> activityModelListToDtoList(List<TaskActivity> activities);

    TaskCommentDto commentModelToDto(TaskComment comment);
    TaskComment commentDtoToModel(TaskCommentDto dto);
    List<TaskCommentDto> commentModelListToDtoList(List<TaskComment> comments);
}