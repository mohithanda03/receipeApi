package com.nl.recipe.service;

import com.nl.recipe.exception.RecipeExistsException;
import com.nl.recipe.exception.RecipeNotFoundException;
import com.nl.recipe.model.Ingredient;
import com.nl.recipe.model.RecipesFilterRequest;
import com.nl.recipe.repository.RecipeRepository;
import com.nl.recipe.model.Recipe;
import com.nl.recipe.repository.RecipeFilterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mohit Handa
 *
 * Type RecipeService handles the api operations on recipes.
 */
@Service
@Slf4j
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final RecipeFilterRepository recipeFilterRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, RecipeFilterRepository recipeFilterRepository){
        this.recipeRepository = recipeRepository;
        this.recipeFilterRepository = recipeFilterRepository;
    }

    /**
     * Adds a recipe to the database
     * @param recipe the recipe to be saved
     * @return
     */
    public Recipe addRecipe(Recipe recipe) {
        if (recipeRepository.existsByRecipeName(recipe.getRecipeName())) {
            log.error("Add recipe Operation, Recipe already found");
            throw new RecipeExistsException("Recipe already exist with " + recipe.getRecipeName());
        }
        log.info("Add recipe operation, Recipe added {}",recipe.getRecipeName());
        return recipeRepository.save(recipe);
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipesList = recipeRepository.findAll();
        log.info("Get all recipe operation, Total recipes found {}",recipesList.size());
        return recipesList;
    }

    /**
     * Updates a Recipe, Gets the existing recipe by id and compares the ingredients and other values updates the recipe
     * with the latest details.
     * @param recipeToBeUpdated The recipe to be updated.
     * @return
     */
    public Recipe updateRecipeById(Recipe recipeToBeUpdated) {
        log.info("Update Operation, recipe to be updated: {}",recipeToBeUpdated.getRecipeId());
        Recipe recipe = getRecipe(recipeToBeUpdated.getRecipeId());
        if (!CollectionUtils.isEmpty(recipeToBeUpdated.getIngredients())) {
            recipe.setIngredients(getDistinctIngredients(recipeToBeUpdated.getIngredients(), recipe.getIngredients()));
        }
        recipe.setCategory(Optional.ofNullable(recipeToBeUpdated.getCategory()).orElse(recipe.getCategory()));
        recipe.setInstructions(Optional.ofNullable(recipeToBeUpdated.getInstructions()).orElse(recipe.getInstructions()));
        recipe.setServings(Optional.of(recipeToBeUpdated.getServings()).orElse(recipe.getServings()));
        return recipeRepository.save(recipe);
    }

    public Recipe getRecipe(String recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe ", recipeId));
    }

    private List<Ingredient> getDistinctIngredients(List<Ingredient> ingredients, List<Ingredient> newIngredients) {
        return new ArrayList<>(Stream.of(ingredients, newIngredients)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Ingredient::getName)))));

    }

    /**
     * Deletes the recipe by id.
     * @param recipeId The recipe Id to be deleted.
     */
    public Boolean deleteRecipeById(String recipeId) {
        log.info("Delete operation, Recipe {}",recipeId);
        if (recipeRepository.existsById(recipeId)){
            recipeRepository.deleteById(recipeId);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * Fetches all the recipes that satisfy the filter criteria.
     * @param request The search criteria
     * @return Filtered list of recipes
     */
    public List<Recipe> searchRecipesByCriteria(RecipesFilterRequest request) {
        log.info("Filter Recipe operation with search criteria {}",request.toString());
        return recipeFilterRepository.filterRecipiesByCriteria(request);
    }

    /**
     * Chosen a category, returns all the recipes under the chosen category.
     * @param categoryType category type
     * @return List of recipes
     */
    public List<Recipe> getRecipesByCategory(String categoryType) {
        log.info("Search by category operation, category {}",categoryType);
        Optional<List<Recipe>> recipeList = recipeRepository.findByCategory(categoryType);
        if (!CollectionUtils.isEmpty(recipeList.get())) {
            return recipeList.get();
        } else {
            throw new RecipeNotFoundException("No recipes found under :"+ categoryType);
        }
    }
}
