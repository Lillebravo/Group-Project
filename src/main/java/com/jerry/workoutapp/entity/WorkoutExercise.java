package com.jerry.workoutapp.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_exercises")
public class WorkoutExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Long workoutExerciseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "rest_time", columnDefinition = "INTEGER")
    private int restTime = 60;

    @Column(name = "order_in_workout", columnDefinition = "INTEGER")
    private int orderIndex;

    @OneToMany(mappedBy = "workoutExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("setNumber ASC")
    private List<WorkoutExerciseSet> sets = new ArrayList<>();

    public WorkoutExercise() {}

    public WorkoutExercise(Workout workout, Exercise exercise, int orderIndex) {
        this.workout = workout;
        this.exercise = exercise;
        this.orderIndex = orderIndex;
    }

    public WorkoutExercise(Workout workout, Exercise exercise, int restTime,
                           int orderIndex, List<WorkoutExerciseSet> sets) {
        this.workout = workout;
        this.exercise = exercise;
        this.restTime = restTime;
        this.orderIndex = orderIndex;
        this.sets = sets;
    }

    public WorkoutExercise(Workout workout, Exercise exercise, int restTime, int orderIndex) {
        this.workout = workout;
        this.exercise = exercise;
        this.restTime = restTime;
        this.orderIndex = orderIndex;
    }

    // Getters and Setters
    public Long getWorkoutExerciseId() {
        return workoutExerciseId;
    }
    public void setWorkoutExerciseId(Long workoutExerciseId) {
        this.workoutExerciseId = workoutExerciseId;
    }

    public Workout getWorkout() {
        return workout;
    }
    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public Exercise getExercise() {
        return exercise;
    }
    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Integer getRestTime() { return restTime; }
    public void setRestTime(Integer restTime) { this.restTime = restTime; }

    public Integer getOrderIndex() {
        return orderIndex;
    }
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public List<WorkoutExerciseSet> getSets() { return sets; }
    public void setSets(List<WorkoutExerciseSet> sets) { this.sets = sets; }
}