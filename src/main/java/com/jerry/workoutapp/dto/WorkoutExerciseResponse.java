package com.jerry.workoutapp.dto;

public class WorkoutExerciseResponse {
    private Long workoutExerciseId;
    private Long exerciseId;
    private String exerciseName;
    private String category;
    private Integer restTime;
    private Integer numberOfSets;
    private Integer orderIndex;

    public WorkoutExerciseResponse() {}

    public WorkoutExerciseResponse(Long workoutExerciseId, Long exerciseId, String exerciseName,
                                   String category, Integer restTime, Integer numberOfSets,
                                   Integer orderIndex) {
        this.workoutExerciseId = workoutExerciseId;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.category = category;
        this.restTime = restTime;
        this.numberOfSets = numberOfSets;
        this.orderIndex = orderIndex;
    }

    // Getters and Setters
    public Long getWorkoutExerciseId() { return workoutExerciseId; }
    public void setWorkoutExerciseId(Long id) { this.workoutExerciseId = id; }

    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getRestTime() { return restTime; }
    public void setRestTime(Integer restTime) { this.restTime = restTime; }

    public Integer getNumberOfSets() { return numberOfSets; }
    public void setNumberOfSets(Integer numberOfSets) { this.numberOfSets = numberOfSets; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}