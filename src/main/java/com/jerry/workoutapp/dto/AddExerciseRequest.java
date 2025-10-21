package com.jerry.workoutapp.dto;

import java.util.List;

public class AddExerciseRequest {
    private Long exerciseId;
    private int restTime;
    private int orderIndex;
    private List<SetRequest> sets;

    public AddExerciseRequest() {}

    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    public int getRestTime() { return restTime; }
    public void setRestTime(int restTime) { this.restTime = restTime; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public List<SetRequest> getSets() { return sets; }
    public void setSets(List<SetRequest> sets) { this.sets = sets; }
}