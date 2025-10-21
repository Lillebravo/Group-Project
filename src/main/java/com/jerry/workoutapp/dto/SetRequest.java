package com.jerry.workoutapp.dto;

public class SetRequest {

    private Integer targetReps;
    private Double targetWeight;

    public SetRequest() {}

    // Getters and Setters
    public Integer getTargetReps() { return targetReps; }
    public void setTargetReps(Integer targetReps) { this.targetReps = targetReps; }

    public Double getTargetWeight() { return targetWeight; }
    public void setTargetWeight(Double targetWeight) { this.targetWeight = targetWeight; }
}
