package com.jerry.workoutapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "workout_exercises")
public class WorkoutExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workout_exercise_id", columnDefinition = "INTEGER")
    private Long workoutExerciseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "sets")
    private Integer sets;

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "order_index")
    private Integer orderIndex;

    public WorkoutExercise() {
    }

    public WorkoutExercise(Workout workout, Exercise exercise, Integer sets, Integer reps, Integer orderIndex) {
        this.workout = workout;
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
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

    public Integer getSets() {
        return sets;
    }
    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }
    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}