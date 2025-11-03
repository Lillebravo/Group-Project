package com.jerry.workoutapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RoutineResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private List<RoutineWorkoutResponse> workouts;

    public RoutineResponse() {}

    public RoutineResponse(Long id, String name, String description, 
                          LocalDateTime createdAt, List<RoutineWorkoutResponse> workouts) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.workouts = workouts;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<RoutineWorkoutResponse> getWorkouts() { return workouts; }
    public void setWorkouts(List<RoutineWorkoutResponse> workouts) { this.workouts = workouts; }

    public static class RoutineWorkoutResponse {
        private Long id;
        private Long workoutId;
        private String workoutName;
        private int dayOrder;
        private String weekDay;
        private String reminderTime;
        private boolean reminderEnabled;

        public RoutineWorkoutResponse() {}

        public RoutineWorkoutResponse(Long id, Long workoutId, String workoutName,
                                    int dayOrder, String weekDay, String reminderTime,
                                    boolean reminderEnabled) {
            this.id = id;
            this.workoutId = workoutId;
            this.workoutName = workoutName;
            this.dayOrder = dayOrder;
            this.weekDay = weekDay;
            this.reminderTime = reminderTime;
            this.reminderEnabled = reminderEnabled;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getWorkoutId() { return workoutId; }
        public void setWorkoutId(Long workoutId) { this.workoutId = workoutId; }

        public String getWorkoutName() { return workoutName; }
        public void setWorkoutName(String workoutName) { this.workoutName = workoutName; }

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