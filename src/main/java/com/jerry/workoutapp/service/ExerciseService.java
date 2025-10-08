package com.jerry.workoutapp.service;

import com.jerry.workoutapp.entity.Exercise;
import com.jerry.workoutapp.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public List<Exercise> searchExercises(String keyword) {
        return exerciseRepository
                .findByNameContainingIgnoreCaseOrMuscleGroupContainingIgnoreCase(keyword, keyword);
    }
}
