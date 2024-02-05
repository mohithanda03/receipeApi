package com.nl.recipe.repository;

import com.nl.recipe.model.Ingredient;
import com.nl.recipe.model.RecipesFilterRequest;
import com.nl.recipe.model.Recipe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Type RecipeFilterRepository Handles the search or filter operation on the existing apis.
 */
@Repository
@Slf4j
public class RecipeFilterRepository{

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public RecipeFilterRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public List<Recipe> filterRecipiesByCriteria(RecipesFilterRequest recipesFilterRequest){
        CriteriaQuery<Recipe> criteriaQuery = criteriaBuilder.createQuery(Recipe.class);
        Root<Recipe> recipeRoot = criteriaQuery.from(Recipe.class);
        ListJoin<Recipe, Ingredient> ingredients = recipeRoot.joinList("ingredients", JoinType.INNER);
        Predicate predicate = getPredicate(recipesFilterRequest,recipeRoot,ingredients);
        criteriaQuery.where(predicate);
        criteriaQuery.distinct(true);
        TypedQuery<Recipe> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    private Predicate getPredicate(RecipesFilterRequest recipesFilterRequest, Root<Recipe> recipeRoot, ListJoin<Recipe,Ingredient> ingredients){
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(recipesFilterRequest.getCategory())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(recipeRoot.get("category")).as(String.class), criteriaBuilder.lower(criteriaBuilder.literal(recipesFilterRequest.getCategory()))));
        }
        if (Objects.nonNull(recipesFilterRequest.getServings())) {
            predicates.add(criteriaBuilder.equal(recipeRoot.get("servings"),  recipesFilterRequest.getServings()));
        }

        if (Objects.nonNull(recipesFilterRequest.getInstructions())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(recipeRoot.get("instructions")).as(String.class), criteriaBuilder.lower(criteriaBuilder.literal(recipesFilterRequest.getInstructions()))));
        }

        if (!CollectionUtils.isEmpty(recipesFilterRequest.getIngredients())) {
            CriteriaBuilder.In<String> inClause= criteriaBuilder.in(criteriaBuilder.lower(ingredients.get("name")).as(String.class));
            CriteriaBuilder.In<String> notInClause= criteriaBuilder.in(criteriaBuilder.lower(ingredients.get("name")).as(String.class));

            recipesFilterRequest.getIngredients().forEach((ingredient, aBoolean) -> {
                if (Boolean.TRUE.equals(aBoolean)) {
                    inClause.value(criteriaBuilder.lower(criteriaBuilder.literal(ingredient)));
                    predicates.add(inClause);
                } else {
                    notInClause.value(criteriaBuilder.lower(criteriaBuilder.literal(ingredient)));
                    predicates.add(criteriaBuilder.not(notInClause));
                }
            });
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
