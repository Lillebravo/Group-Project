package com.jerry.workoutapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "workout_exercise_sets")
public class WorkoutExerciseSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_exercise_id", nullable = false)
    private WorkoutExercise workoutExercise;

    @Column(name = "set_number", nullable = false)
    private int setNumber;

    @Column(name = "target_reps", nullable = false)
    private int targetReps;

    @Column(name = "target_weight", nullable = false)
    private double targetWeight;

    public WorkoutExerciseSet() {}

    public WorkoutExerciseSet(WorkoutExercise workoutExercise, int setNumber,
                              int targetReps, double targetWeight) {
        this.workoutExercise = workoutExercise;
        this.setNumber = setNumber;
        this.targetReps = targetReps;
        this.targetWeight = targetWeight;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WorkoutExercise getWorkoutExercise() { return workoutExercise; }
    public void setWorkoutExercise(WorkoutExercise we) { this.workoutExercise = we; }

    public Integer getSetNumber() { return setNumber; }
    public void setSetNumber(Integer setNumber) { this.setNumber = setNumber; }

    public Integer getTargetReps() { return targetReps; }
    public void setTargetReps(Integer reps) { this.targetReps = reps; }

    public Double getTargetWeight() { return targetWeight; }
    public void setTargetWeight(Double weight) { this.targetWeight = weight; }
}