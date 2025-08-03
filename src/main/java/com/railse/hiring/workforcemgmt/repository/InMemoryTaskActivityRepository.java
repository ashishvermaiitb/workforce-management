package com.railse.hiring.workforcemgmt.repository;

import com.railse.hiring.workforcemgmt.model.TaskActivity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTaskActivityRepository implements TaskActivityRepository {
    private final Map<Long, TaskActivity> activityStore = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public TaskActivity save(TaskActivity activity) {
        if (activity.getId() == null) {
            activity.setId(idCounter.incrementAndGet());
        }
        if (activity.getTimestamp() == null) {
            activity.setTimestamp(LocalDateTime.now());
        }
        activityStore.put(activity.getId(), activity);
        return activity;
    }

    @Override
    public List<TaskActivity> findByTaskIdOrderByTimestamp(Long taskId) {
        return activityStore.values().stream()
                .filter(activity -> activity.getTaskId().equals(taskId))
                .sorted((a1, a2) -> a1.getTimestamp().compareTo(a2.getTimestamp()))
                .collect(Collectors.toList());
    }
}