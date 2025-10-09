package com.jerry.workoutapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class WorkoutResponse {
    private Long workoutId;
    private String name;
    private Long userId;
    private LocalDateTime createdAt;
    private Integer exerciseCount;
    private List<WorkoutExerciseResponse> exercises;

    public WorkoutResponse(Long workoutId, String name, Long userId, LocalDateTime createdAt,
                           Integer exerciseCount, List<WorkoutExerciseResponse> exercises) {
        this.workoutId = workoutId;
        this.name = name;
        this.userId = userId;
        this.createdAt = createdAt;
        this.exerciseCount = exerciseCount;
        this.exercises = exercises;
    }

    // Getters and Setters
    public Long getWorkoutId() {
        return workoutId;
    }
    public void setWorkoutId(Long workoutId) {
        this.workoutId = workoutId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getExerciseCount() {
        return exerciseCount;
    }
    public void setExerciseCount(Integer exerciseCount) {
        this.exerciseCount = exerciseCount;
    }

    public List<WorkoutExerciseResponse> getExercises() {
        return exercises;
    }
    public void setExercises(List<WorkoutExerciseResponse> exercises) {
        this.exercises = exercises;
    }
}