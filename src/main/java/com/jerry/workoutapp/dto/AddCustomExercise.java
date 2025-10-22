package com.jerry.workoutapp.dto;

import jakarta.validation.constraints.NotBlank;

public class AddCustomExercise {

    @NotBlank(message = "Övningsnamn är obligatoriskt")
    private String name;

    @NotBlank(message = "Muskelgrupp är obligatorisk")
    private String category;

    private String description;


    public AddCustomExercise() {}

    public AddCustomExercise(String name, String category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}