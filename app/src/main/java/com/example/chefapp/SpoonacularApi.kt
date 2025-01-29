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
        @Query("number") number: Int = 100,
        @Query("ranking") ranking: Int = 1,
        @Query("ignorePantry") ignorePantry: Boolean = true,
        @Query("apiKey") apiKey: String
    ): Call<List<Recipe>>

    // Wyszukiwanie przepisów na podstawie tekstu z uwzględnieniem alergii
    @GET("recipes/complexSearch")
    fun searchRecipesByText(
        @Query("query") query: String, // Słowo kluczowe do wyszukiwania przepisów (np. "chicken")
        @Query("intolerances") intolerances: String? = null, // Nietolerancje (np. "gluten")
        @Query("sort") sort: String? = null, // Parametr sortowania (np. "popularity" lub "rating")
        @Query("number") number: Int? = 100, // Liczba wyników do zwrócenia
        @Query("addRecipeInformation") addRecipeInformation:Boolean=true,
        @Query("apiKey") apiKey: String // Klucz API do autentykacji
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
