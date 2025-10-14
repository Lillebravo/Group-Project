package com.jerry.workoutapp.dto;

public class UpdateWorkoutExerciseRequest {
    private Integer sets;
    private Integer reps;

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }
}
