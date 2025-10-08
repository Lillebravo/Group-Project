package com.jerry.workoutapp.service;

import com.jerry.workoutapp.entity.Workout;
import com.jerry.workoutapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

    public Workout createWorkout(String name) {
        Workout workout = new Workout(name);
        return workoutRepository.save(workout);
    }
}