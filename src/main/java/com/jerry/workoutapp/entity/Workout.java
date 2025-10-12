package com.jerry.workoutapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workouts")
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workout_id", columnDefinition = "INTEGER")
    private Long workoutId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC") // Sort in db directly
    private List<WorkoutExercise> workoutExercises = new ArrayList<>();

    public Workout() {
        this.createdAt = LocalDateTime.now();
    }

    public Workout(String name, User user) {
        this.name = name;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    // Helper method to add exercise to workout
    public void addExercise(Exercise exercise, Integer sets, Integer reps, Integer orderIndex) {
        WorkoutExercise workoutExercise = new WorkoutExercise(this, exercise, sets, reps, orderIndex);
        workoutExercises.add(workoutExercise);
    }

    // Helper method to remove exercise from workout
    public void removeExercise(WorkoutExercise workoutExercise) {
        workoutExercises.remove(workoutExercise);
        workoutExercise.setWorkout(null);
    }

    // Getters and Setters
    public Long getWorkoutId() { return workoutId; }
    public void setWorkoutId(Long workoutId) { this.workoutId = workoutId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<WorkoutExercise> getWorkoutExercises() { return workoutExercises; }
    public void setWorkoutExercises(List<WorkoutExercise> workoutExercises) {
        this.workoutExercises = workoutExercises;
    }
}