package com.nl.recipe.repository;

import com.nl.recipe.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, String> {

    @Query(value = "SELECT r FROM Recipe r WHERE LOWER(r.category) = LOWER(CAST(?1 AS STRING))")
    Optional<List<Recipe>> findByCategory(String category);

    boolean existsByRecipeName(String recipeName);
}