package com.jerry.workoutapp.dto;

public class CreateWorkoutRequest {
    private Long userId;
    private String name;

    public CreateWorkoutRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}