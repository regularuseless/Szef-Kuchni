package com.example.chefapp

import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Response

class SearchApiTest {

    private val mockApi: SpoonacularApi = mockk()
    private val mockCallSearchByIngredients: Call<List<Recipe>> = mockk()
    private val mockCallSearchByText: Call<RecipeSearchResponse> = mockk()
    private val mockCallDetails: Call<RecipeDetails> = mockk()
    private val mockCallInstructions: Call<List<AnalyzedInstruction>> = mockk()

    private val apiKey = "c00df3c343d14c7390f49b9adc0c1cfe"

    @Before
    fun setUp() {
        mockkStatic(SpoonacularApi::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // Testowanie zapytania searchRecipesByIngredients
    @Test
    fun `searchRecipesByIngredients should call API with correct parameters`() {
        val ingredients = "tomato,cheese"
        val number = 5
        val ranking = 1
        val ignorePantry = true

        every { mockApi.searchRecipesByIngredients(any(), any(), any(), any(), any()) } returns mockCallSearchByIngredients

        mockApi.searchRecipesByIngredients(ingredients, number, ranking, ignorePantry, apiKey)

        verify {
            mockApi.searchRecipesByIngredients(
                ingredients = "tomato,cheese",
                number = 5,
                ranking = 1,
                ignorePantry = true,
                apiKey = apiKey
            )
        }
    }

    // Testowanie zapytania searchRecipesByText
    @Test
    fun `searchRecipesByText should call API with correct parameters`() {
        val query = "pasta"
        val intolerances = "gluten,dairy"
        val sort = "popularity"
        val number = 100
        val addRecipeInformation = true
        val fillIngredients = true
        val addRecipeNutrition = true

        // Upewnij się, że parametr "fillIngredients" istnieje w metodzie
        every { mockApi.searchRecipesByText(any(), any(), any(), any(), any(), any(), any(), any()) } returns mockCallSearchByText

        mockApi.searchRecipesByText(query, intolerances, sort, number, addRecipeInformation, fillIngredients, addRecipeNutrition, apiKey)

        verify {
            mockApi.searchRecipesByText(
                query = "pasta",
                intolerances = "gluten,dairy",
                sort = "popularity",
                number = 100,
                addRecipeInformation = true,
                addRecipeNutrition = true,
                apiKey = apiKey
            )
        }
    }

    // Testowanie zapytania getRecipeDetails
    @Test
    fun `getRecipeDetails should call API with correct parameters`() {
        val recipeId = 1
        val includeNutrition = true

        every { mockApi.getRecipeDetails(any(), any(), any()) } returns mockCallDetails

        mockApi.getRecipeDetails(recipeId, includeNutrition, apiKey)

        verify {
            mockApi.getRecipeDetails(
                recipeId = 1,
                includeNutrition = true,
                apiKey = apiKey
            )
        }
    }

    // Testowanie zapytania getAnalyzedInstructions
    @Test
    fun `getAnalyzedInstructions should call API with correct parameters`() {
        val recipeId = 1

        every { mockApi.getAnalyzedInstructions(any(), any()) } returns mockCallInstructions

        mockApi.getAnalyzedInstructions(recipeId, apiKey)

        verify {
            mockApi.getAnalyzedInstructions(
                recipeId = 1,
                apiKey = apiKey
            )
        }
    }
}
