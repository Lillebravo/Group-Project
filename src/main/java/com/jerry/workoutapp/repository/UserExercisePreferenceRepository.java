package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.UserExercisePreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserExercisePreferenceRepository extends JpaRepository<UserExercisePreference, Long> {
    Optional<UserExercisePreference> findByUser_UserIdAndExercise_ExerciseId(
            Long userId, Long exerciseId);

    List<UserExercisePreference> findByUser_UserIdAndIsFavourite(
            Long userId, int isFavourite);
}
