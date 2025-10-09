package com.jerry.workoutapp.dto;

public class ReorderExerciseDto {
    private Long workoutExerciseId;
    private Integer newOrderIndex;

    public ReorderExerciseDto() {}

    public ReorderExerciseDto(Long workoutExerciseId, Integer newOrderIndex) {
        this.workoutExerciseId = workoutExerciseId;
        this.newOrderIndex = newOrderIndex;
    }

    public Long getWorkoutExerciseId() {
        return workoutExerciseId;
    }

    public void setWorkoutExerciseId(Long workoutExerciseId) {
        this.workoutExerciseId = workoutExerciseId;
    }

    public Integer getNewOrderIndex() {
        return newOrderIndex;
    }

    public void setNewOrderIndex(Integer newOrderIndex) {
        this.newOrderIndex = newOrderIndex;
    }
}