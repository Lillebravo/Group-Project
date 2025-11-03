package com.jerry.workoutapp.dto;

import java.util.List;

public class CreateRoutineRequest {
    private String name;
    private String description;
    private List<RoutineWorkoutDto> workouts;

    public CreateRoutineRequest() {}

    public CreateRoutineRequest(String name, String description, List<RoutineWorkoutDto> workouts) {
        this.name = name;
        this.description = description;
        this.workouts = workouts;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<RoutineWorkoutDto> getWorkouts() { return workouts; }
    public void setWorkouts(List<RoutineWorkoutDto> workouts) { this.workouts = workouts; }

    public static class RoutineWorkoutDto {
        private Long workoutId;
        private int dayOrder;
        private String weekDay;
        private String reminderTime;
        private boolean reminderEnabled = true;

        public RoutineWorkoutDto() {}

        // Getters and Setters
        public Long getWorkoutId() { return workoutId; }
        public void setWorkoutId(Long workoutId) { this.workoutId = workoutId; }

        public int getDayOrder() { return dayOrder; }
        public void setDayOrder(int dayOrder) { this.dayOrder = dayOrder; }

        public String getWeekDay() { return weekDay; }
        public void setWeekDay(String weekDay) { this.weekDay = weekDay; }

        public String getReminderTime() { return reminderTime; }
        public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }

        public boolean isReminderEnabled() { return reminderEnabled; }
        public void setReminderEnabled(boolean reminderEnabled) {
            this.reminderEnabled = reminderEnabled;
        }
    }
}