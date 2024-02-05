package com.nl.recipe.utils;

import com.nl.recipe.model.Ingredient;
import com.nl.recipe.model.Recipe;
import lombok.experimental.UtilityClass;
import org.assertj.core.util.Lists;

import java.util.UUID;

@UtilityClass
public class RecipeFactory {

    public static Recipe getRecipe() {
        return Recipe.builder()
                .recipeName("Banana Bread")
                .servings(2)
                .instructions("Oven")
                .category("Desert")
                .ingredients(Lists.newArrayList(new Ingredient("Flour"),
                        new Ingredient("Banana"),
                        new Ingredient("Sugar")))
                .build();
    }

    public static Recipe getSavedRecipe() {
        return Recipe.builder()
                .recipeName("Banana Bread")
                .servings(2)
                .instructions("Oven")
                .category("Desert")
                .recipeId(UUID.randomUUID().toString())
                .ingredients(Lists.newArrayList(new Ingredient(UUID.randomUUID(), "Flour"),
                        new Ingredient(UUID.randomUUID(), "Banana"),
                        new Ingredient(UUID.randomUUID(), "Sugar")))
                .build();
    }
}
