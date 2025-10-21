package com.jerry.workoutapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workout_exercise_logs")
public class WorkoutExerciseLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_log_id", nullable = false)
    private WorkoutLog workoutLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "set_number", nullable = false, columnDefinition = "INTEGER")
    private int setNumber;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(name = "reps", nullable = false, columnDefinition = "INTEGER")
    private int reps;

    @Column(name = "estimated_1rm")
    private double estimated1RM;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public WorkoutExerciseLog() {
        this.createdAt = LocalDateTime.now();
    }

    public WorkoutExerciseLog(WorkoutLog workoutLog, Exercise exercise) {
        this.workoutLog = workoutLog;
        this.exercise = exercise;
        this.createdAt = LocalDateTime.now();
    }

    public WorkoutExerciseLog(WorkoutLog workoutLog, Exercise exercise
            , int setNumber, double weight, int reps) {
        this.workoutLog = workoutLog;
        this.exercise = exercise;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.createdAt = LocalDateTime.now();
    }

    public WorkoutExerciseLog(WorkoutLog workoutLog, Exercise exercise
            , int setNumber, double weight, int reps, String notes) {
        this.workoutLog = workoutLog;
        this.exercise = exercise;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    public WorkoutExerciseLog(WorkoutLog workoutLog, Exercise exercise
            , int setNumber, double weight, int reps
            , double estimated1RM, String notes) {
        this.workoutLog = workoutLog;
        this.exercise = exercise;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.estimated1RM = estimated1RM;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WorkoutLog getWorkoutLog() { return workoutLog; }
    public void setWorkoutLog(WorkoutLog workoutLog) { this.workoutLog = workoutLog; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public int getSetNumber() { return setNumber; }
    public void setSetNumber(int setNumber) { this.setNumber = setNumber; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public double getEstimated1RM() { return estimated1RM; }
    public void setEstimated1RM(double estimated1RM) { this.estimated1RM = estimated1RM; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}