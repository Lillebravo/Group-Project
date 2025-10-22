package com.jerry.workoutapp.dto;

import com.jerry.workoutapp.entity.WorkoutExerciseSet;

import java.util.List;

public class WorkoutExerciseSetResponse {
    private Long id;
    private Long workoutExerciseId;
    private Integer setNumber;
    private Integer targetReps;
    private Double targetWeight;

    public WorkoutExerciseSetResponse() {}

    public WorkoutExerciseSetResponse(Long id, Long workoutExerciseId, Integer setNumber,
                                      Integer targetReps, Double targetWeight) {
        this.id = id;
        this.workoutExerciseId = workoutExerciseId;
        this.setNumber = setNumber;
        this.targetReps = targetReps;
        this.targetWeight = targetWeight;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWorkoutExerciseId() { return workoutExerciseId; }
    public void setWorkoutExerciseId(Long workoutExerciseId) { this.workoutExerciseId = workoutExerciseId; }

    public Integer getSetNumber() { return setNumber; }
    public void setSetNumber(Integer setNumber) { this.setNumber = setNumber; }

    public Integer getTargetReps() { return targetReps; }
    public void setTargetReps(Integer targetReps) { this.targetReps = targetReps; }

    public Double getTargetWeight() { return targetWeight; }
    public void setTargetWeight(Double targetWeight) { this.targetWeight = targetWeight; }

    public static List<WorkoutExerciseSetResponse> convert(List<WorkoutExerciseSet> sets) {
        return sets.stream().map((set) -> new WorkoutExerciseSetResponse(
                set.getId(),
                set.getWorkoutExercise().getWorkoutExerciseId(),
                set.getSetNumber(),
                set.getTargetReps(),
                set.getTargetWeight()
        )).toList();
    }
}