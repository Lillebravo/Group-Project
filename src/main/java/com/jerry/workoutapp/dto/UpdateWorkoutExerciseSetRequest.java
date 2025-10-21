package com.jerry.workoutapp.dto;

public class UpdateWorkoutExerciseSetRequest {
    private Integer targetReps;
    private Double targetWeight;

    public UpdateWorkoutExerciseSetRequest() {}

    public Integer getTargetReps() { return targetReps; }
    public void setTargetReps(Integer targetReps) { this.targetReps = targetReps; }

    public Double getTargetWeight() { return targetWeight; }
    public void setTargetWeight(Double targetWeight) { this.targetWeight = targetWeight; }
}
