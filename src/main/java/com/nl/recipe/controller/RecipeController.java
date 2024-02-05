package com.nl.recipe.controller;

import com.nl.recipe.model.Recipe;
import com.nl.recipe.model.RecipesFilterRequest;
import com.nl.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Mohit Handa
 * Type Recipr Controller Exposes end points.
 */
@Tag(name="Recipe API", description = "Recipe management APIs")
@RestController
public class RecipeController{

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
    }

    /**
     * Creates a new Recipe.
     * @return the saved recipe.
     */
    @Operation(summary = "Create a new Recipe")
    @PostMapping(path ="/recipe",  consumes = "application/json", produces = "application/json")
    public ResponseEntity<Recipe> addRecipe(@RequestBody @Valid Recipe recipe) {
        return ResponseEntity.ok(recipeService.addRecipe(recipe));
    }

    /**
     * Gets all the recipes from database.
     * @return List of all available recipes.
     */
    @Operation(summary = "Retrieve all the Recipes")
    @GetMapping(path = "/recipes", produces = "application/json")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    /**
     * Gets all the recipes under a given category.
     * @param categoryType Category type.
     * @return List of recipes under a category.
     *
     */
    @Operation(summary = "Search for recipes by a category",
     description = "Searches the recipes under the category")
    @GetMapping(path = "/recipes/category/{category}", produces = "application/json")
    public ResponseEntity<List<Recipe>> getRecipesByCategory(@PathVariable("category") String categoryType) {
        return ResponseEntity.ok(recipeService.getRecipesByCategory(categoryType));
    }

    /**
     * Updates a recipe.
     * @param recipeTobeUpdated Recipe to be updated.
     * @return The updated recipe.
     */
    @Operation(summary = "Update a recipe",
     description = "Finds a recipe by receipeId and updates it.")
    @ApiResponse(responseCode = "200", description = "Recipe updated successfully")
    @PutMapping(path = "/recipe", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> updateRecipeById(@RequestBody Recipe recipeTobeUpdated) {
        recipeService.updateRecipeById(recipeTobeUpdated);
        return ResponseEntity.ok("Recipe updated successfully.");
    }

    /**
     * Deletes a recipe with given Id.
     * @param recipeId RecipeId
     * @return
     */
    @Operation(summary = "Deletes a recipe by its Id")
    @ApiResponse(responseCode = "200", description = "Recipe deleted successfully")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    @DeleteMapping(path = "/recipe/{recipeId}", produces = "application/json")
    public ResponseEntity<String> removeRecipeById(@PathVariable("recipeId") @Pattern(regexp = "^[a-zA-Z0-9-]+$") String recipeId) {
        boolean isDeleted = recipeService.deleteRecipeById(recipeId);
        if (isDeleted){
            return ResponseEntity.ok()
                    .body("Recipe deleted successfully.");        }
        else {
            return ResponseEntity.notFound().build();        }
    }

    /**
     * Gets all the recipes that satisfy the search criteria.
     * @param recipesSearchRequest Filter criteria to search for recipe.
     * @return List of recipes.
     */
    @Operation(summary = "Search for Recipes with filter criteria",
        description="Given some filter criteria like ingredients, instructions, servings, returns all the recipes matching the filter criteria")
    @PostMapping(path ="/search/recipes",consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Recipe>> searchRecipesByCriteria(@RequestBody RecipesFilterRequest recipesSearchRequest) {
        return ResponseEntity.ok(recipeService.searchRecipesByCriteria(recipesSearchRequest));
    }
}
