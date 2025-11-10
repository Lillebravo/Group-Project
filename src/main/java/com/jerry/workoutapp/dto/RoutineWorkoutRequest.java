package com.jerry.workoutapp.dto;

public class RoutineWorkoutRequest {
    private Long workoutId;
    private String weekDay;
    private Integer dayOrder;

    // --- Getters och Setters ---
    public Long getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Long workoutId) {
        this.workoutId = workoutId;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public Integer getDayOrder() {
        return dayOrder;
    }

    public void setDayOrder(Integer dayOrder) {
        this.dayOrder = dayOrder;
    }
}
