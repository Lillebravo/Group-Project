package com.jerry.workoutapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkoutAppApplication {

    public static void main(String[] args) {

        SpringApplication.run(WorkoutAppApplication.class, args);

        System.out.println("Workout Application Started");
    }

}
