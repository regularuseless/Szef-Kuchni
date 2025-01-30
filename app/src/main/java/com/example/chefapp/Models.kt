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
    val pricePerServing: Double,
    val nutrition: Nutrition,  // Dodane pole dla wartości odżywczych
    val sourceName: String,
    val summary: String,
    val diets: List<String>,
    val extendedIngredients: List<Ingredient> // Dodane pole dla składników
)

data class Nutrition(
    val nutrients: List<Nutrients>  // Tylko kalorie
)

data class Nutrients(
    val name: String,
    val amount: Double,
    val unit: String,
    val percentOfDailyNeeds:Double
)


data class RecipeDetails(
    val id: Int,
    val title: String,
    val image: String,
    val instructions: String?,
    val extendedIngredients: List<Ingredient>,
    val nutrition: Nutrition,
    val summary: String,
    val diets: List<String>,
    val sourceName: String,
    val pricePerServing: Double,
    val readyInMinutes: Int,
    val analyzedInstructions: List<AnalyzedInstruction>,

    )

data class Ingredient(
    val id: Int,
    val name: String,
    var amount: Double,
    val unit: String
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
