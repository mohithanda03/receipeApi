package com.nl.recipe.model;

import lombok.Data;

import java.util.Map;

@Data
public class RecipesFilterRequest {
    private String category;
    private Integer servings;
    private String instructions;
    private Map<String, Boolean> ingredients;
}
