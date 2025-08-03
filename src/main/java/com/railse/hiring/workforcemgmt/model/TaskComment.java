package com.railse.hiring.workforcemgmt.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class TaskComment {
    // Getters and Setters
    private Long id;
    private Long taskId;
    private String comment;
    private Long userId;
    private LocalDateTime timestamp;

    // Constructors
    public TaskComment() {}

}