package com.railse.hiring.workforcemgmt.model;

import com.railse.hiring.workforcemgmt.model.enums.ActivityType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class TaskActivity {
    // Getters and Setters
    private Long id;
    private Long taskId;
    private ActivityType activityType;
    private String description;
    private Long userId;
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;

    // Constructors
    public TaskActivity() {}

}