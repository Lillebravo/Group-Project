package com.jerry.workoutapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_exercise_preferences")
public class UserExercisePreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "is_favourite", columnDefinition = "INTEGER")
    private int isFavourite = 0;

    @Column(name = "is_custom", columnDefinition = "INTEGER")
    private int isCustom = 0;

    @Column(name = "default_weight")
    private double defaultWeight;

    @Column(name = "default_rest_time", columnDefinition = "INTEGER")
    private int defaultRestTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UserExercisePreference() { this.createdAt = LocalDateTime.now(); }

    public UserExercisePreference(User user, Exercise exercise
            , int isFavourite, int isCustom, double defaultWeight
            , int defaultRestTime, LocalDateTime createdAt) {
        this.user = user;
        this.exercise = exercise;
        this.isFavourite = isFavourite;
        this.isCustom = isCustom;
        this.defaultWeight = defaultWeight;
        this.defaultRestTime = defaultRestTime;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public int getIsFavourite() { return isFavourite; }
    public void setIsFavourite() { isFavourite = 1; }

    public int getIsCustom() { return isCustom; }
    public void setIsCustom() { isCustom = 1; }

    public double getDefaultWeight() { return defaultWeight; }
    public void setDefaultWeight(double defaultWeight) { this.defaultWeight = defaultWeight; }

    public int getDefaultRestTime() { return defaultRestTime; }
    public void setDefaultRestTime(int defaultRestTime) { this.defaultRestTime = defaultRestTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}