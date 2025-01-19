package com.example.chefapp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApi {

    // Wyszukiwanie przepisów na podstawie składników
    @GET("recipes/findByIngredients")
    fun searchRecipesByIngredients(
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 10,
        @Query("ranking") ranking: Int = 1,
        @Query("ignorePantry") ignorePantry: Boolean = true,
        @Query("apiKey") apiKey: String
    ): Call<List<Recipe>>

    // Wyszukiwanie przepisów na podstawie tekstu z uwzględnieniem alergii
    @GET("recipes/complexSearch")
    fun searchRecipesByText(
        @Query("query") query: String,
        @Query("intolerances") intolerances: String? = null,
        @Query("number") number: Int = 10,
        @Query("addRecipeNutrition") addRecipeNutrition: Boolean = true,
        @Query("apiKey") apiKey: String
    ): Call<RecipeSearchResponse>

    // Pobieranie szczegółowych informacji o przepisie
    @GET("recipes/{id}/information")
    fun getRecipeDetails(
        @Path("id") recipeId: Int,
        @Query("includeNutrition") includeNutrition: Boolean = true,
        @Query("apiKey") apiKey: String
    ): Call<RecipeDetails>

    // Pobieranie szczegółowych instrukcji przygotowania
    @GET("recipes/{id}/analyzedInstructions")
    fun getAnalyzedInstructions(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String
    ): Call<List<AnalyzedInstruction>>
}
