package com.jerry.workoutapp.controller;


import com.jerry.workoutapp.dto.CreateRoutineRequest;
import com.jerry.workoutapp.dto.RoutineResponse;
import com.jerry.workoutapp.service.RoutineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routines")
public class RoutineController {

    private final RoutineService routineService;

    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }

    // Create a new routine for a user
    // POST /api/routines

    @PostMapping
    public ResponseEntity<RoutineResponse> createRoutine(@RequestBody CreateRoutineRequest routineRequest) {
        RoutineResponse createdRoutine = routineService.createRoutine(
                routineRequest.getName (),
                routineRequest.getDescription()
        );

        return ResponseEntity.ok(createdRoutine);
    }
}
