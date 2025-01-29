package com.example.chefapp
data class RecipeSearchResponse(
    val results: List<Recipe>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)
data class Recipe(
    val id: Int,
    var title: String,
    val image: String,
    var readyInMinutes: Int,
    val pricePerServing: Double
)

data class RecipeDetails(
    val id: Int,
    val title: String,
    val image: String,
    val instructions: String?,
    val extendedIngredients: List<Ingredient>,
    val nutrition: Nutrition,
    val pricePerServing: Double,
    val readyInMinutes: Int,
    val analyzedInstructions: List<AnalyzedInstruction>,
    val glutenFree: Boolean,   // Dodane pole
    val vegan: Boolean,       // Dodane pole
    val vegetarian: Boolean,  // Dodane pole
    val dairyFree: Boolean,   // Dodane pole
    val veryHealthy: Boolean, // Dodane pole
    val cheap: Boolean        // Dodane pole
)

data class Ingredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String
)

data class Nutrition(
    val nutrients: List<Nutrient>
)

data class Nutrient(
    val name: String,
    val amount: Double,
    val unit: String
)

data class AnalyzedInstruction(
    val name: String,
    val steps: List<InstructionStep>
)

data class InstructionStep(
    val number: Int,
    val step: String,
    val ingredients: List<Ingredient>,
    val equipment: List<Equipment>,
    val length: Length?
)

data class Equipment(
    val id: Int,
    val name: String,
    val image: String,
    val temperature: Temperature?
)

data class Temperature(
    val number: Double,
    val unit: String
)

data class Length(
    val number: Int,
    val unit: String
)
