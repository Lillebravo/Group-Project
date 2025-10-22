package com.jerry.workoutapp.dto;

import com.jerry.workoutapp.entity.Exercise;
import com.jerry.workoutapp.entity.WorkoutExercise;

import java.util.List;

public class WorkoutExerciseResponse {
    private Long workoutExerciseId;
    private Long exerciseId;
    private String exerciseName;
    private String category;
    private Integer restTime;
    private List<WorkoutExerciseSetResponse> sets;
    private Integer orderIndex;

    public WorkoutExerciseResponse() {}

    public WorkoutExerciseResponse(Long workoutExerciseId, Long exerciseId, String exerciseName,
                                   String category, Integer restTime, List<WorkoutExerciseSetResponse> sets,
                                   Integer orderIndex) {
        this.workoutExerciseId = workoutExerciseId;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.category = category;
        this.restTime = restTime;
        this.sets = sets;
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

    public List<WorkoutExerciseSetResponse> getSets() { return sets; }
    public void setSets(List<WorkoutExerciseSetResponse> sets) { this.sets = sets; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public static WorkoutExerciseResponse convert(WorkoutExercise we) {
        Exercise exercise = we.getExercise();
        return new WorkoutExerciseResponse(
                we.getWorkoutExerciseId(),
                exercise.getExerciseId(),
                exercise.getName(),
                exercise.getCategory(),
                we.getRestTime(),
                WorkoutExerciseSetResponse.convert(we.getSets()),
                we.getOrderIndex()
        );
    }
}