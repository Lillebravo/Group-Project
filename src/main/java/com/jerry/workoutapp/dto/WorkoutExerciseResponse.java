package com.jerry.workoutapp.dto;

public class WorkoutExerciseResponse {
    private Long workoutExerciseId;
    private Long exerciseId;
    private String exerciseName;
    private String muscleGroup;
    private Integer sets;
    private Integer reps;
    private Integer orderIndex;

    public WorkoutExerciseResponse() {
    }

    public WorkoutExerciseResponse(Long workoutExerciseId, Long exerciseId, String exerciseName,
                                   String muscleGroup, Integer sets, Integer reps, Integer orderIndex) {
        this.workoutExerciseId = workoutExerciseId;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.muscleGroup = muscleGroup;
        this.sets = sets;
        this.reps = reps;
        this.orderIndex = orderIndex;
    }

    public Long getWorkoutExerciseId() {
        return workoutExerciseId;
    }

    public void setWorkoutExerciseId(Long workoutExerciseId) {
        this.workoutExerciseId = workoutExerciseId;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

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

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}