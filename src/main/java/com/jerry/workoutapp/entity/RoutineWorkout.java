package com.jerry.workoutapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "routine_workouts")
public class RoutineWorkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @Column(name = "day_order", nullable = false, columnDefinition = "INTEGER")
    private int dayOrder;

    @Column(name = "week_day")
    private String weekDay;

    @Column(name = "reminder_time")
    private String reminderTime;  // Saved as TEXT in SQLite

    @Column(name = "reminder_enabled", columnDefinition = "INTEGER")
    private int reminderEnabled = 1;

    public RoutineWorkout() {}

    public RoutineWorkout(Routine routine, Workout workout) {
        this.routine = routine;
        this.workout = workout;
    }

    public RoutineWorkout(Routine routine, Workout workout, int dayOrder, String weekDay) {
        this.routine = routine;
        this.workout = workout;
        this.dayOrder = dayOrder;
        this.weekDay = weekDay;
    }

    public RoutineWorkout(Routine routine, Workout workout
            , int dayOrder, String weekDay, String reminderTime, int reminderEnabled) {
        this.routine = routine;
        this.workout = workout;
        this.dayOrder = dayOrder;
        this.weekDay = weekDay;
        this.reminderTime = reminderTime;
        this.reminderEnabled = reminderEnabled;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Routine getRoutine() { return routine; }
    public void setRoutine(Routine routine) { this.routine = routine; }

    public Workout getWorkout() { return workout; }
    public void setWorkout(Workout workout) { this.workout = workout; }

    public int getDayOrder() { return dayOrder; }
    public void setDayOrder(int dayOrder) { this.dayOrder = dayOrder; }

    public String getWeekDay() { return weekDay; }
    public void setWeekDay(String weekDay) { this.weekDay = weekDay; }

    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }

    public int getReminderEnabled() { return reminderEnabled; }
    public void setReminderEnabled(int reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
    }
}