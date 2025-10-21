package com.jerry.workoutapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_logs")
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @Column(name = "routine_day", columnDefinition = "INTEGER")
    private int routineDay;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_minutes", columnDefinition = "INTEGER")
    private int durationMinutes;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "workoutLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutExerciseLog> exerciseLogs = new ArrayList<>();

    public WorkoutLog() {
        this.createdAt = LocalDateTime.now();
    }

    public WorkoutLog(User user, Workout workout, Routine routine) {
        this.user = user;
        this.workout = workout;
        this.routine = routine;
        this.createdAt = LocalDateTime.now();
    }

    public WorkoutLog(User user, Workout workout, Routine routine
            , int routineDay, LocalDateTime startedAt, LocalDateTime completedAt
            , int durationMinutes) {
        this.user = user;
        this.workout = workout;
        this.routine = routine;
        this.routineDay = routineDay;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationMinutes = durationMinutes;
        this.createdAt = LocalDateTime.now();
    }

    public WorkoutLog(User user, Workout workout, Routine routine
            , int routineDay, LocalDateTime startedAt, LocalDateTime completedAt
            , int durationMinutes, String notes) {
        this.user = user;
        this.workout = workout;
        this.routine = routine;
        this.routineDay = routineDay;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Workout getWorkout() { return workout; }
    public void setWorkout(Workout workout) { this.workout = workout; }

    public Routine getRoutine() { return routine; }
    public void setRoutine(Routine routine) { this.routine = routine; }

    public int getRoutineDay() { return routineDay; }
    public void setRoutineDay(int routineDay) { this.routineDay = routineDay; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<WorkoutExerciseLog> getExerciseLogs() { return exerciseLogs; }
    public void setExerciseLogs(List<WorkoutExerciseLog> exerciseLogs) {
        this.exerciseLogs = exerciseLogs;
    }
}