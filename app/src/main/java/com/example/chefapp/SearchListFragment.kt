package com.example.chefapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SearchListFragment : Fragment() {
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val detailedRecipeViewModel: DetailedRecipeViewModel by activityViewModels()

    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_dishes)

        recipeAdapter = RecipeAdapter(recipesList) { recipe ->
            openDishFragment(recipe)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recipeAdapter

        searchViewModel.searchParameters.observe(viewLifecycleOwner) { params ->
            performSearch(params)
        }

        return view
    }

    fun openDishFragment(recipe: Recipe) {
        detailedRecipeViewModel.selectRecipe(recipe)
        val fragment = DishFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun searchRecipesByText(query: String, intolerances: String? = null, sort: String? = null) {
        RetrofitInstance.api.searchRecipesByText(query, intolerances, sort, apiKey = apiKey)
            .enqueue(object : Callback<RecipeSearchResponse> {
                override fun onResponse(
                    call: Call<RecipeSearchResponse>,
                    response: Response<RecipeSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val recipes = response.body()?.results ?: emptyList()
                        recipesList.clear()
                        recipesList.addAll(recipes)
                        /*runBlocking {
                            for(index in recipesList.indices){
                                recipesList[index].title =
                                    translate(recipesList[index].title, "en", "pl")
                                Log.d("Tlumaczenie", recipesList[index].extendedIngredients[0].name)
                            }
                        }*/
                        recipeAdapter.notifyDataSetChanged()
                    } else {
                        // Obsługa różnych kodów błędów
                        val errorMessage = when (response.code()) {
                            402 -> "Koniec tokenów API - skontaktuj się z administratorem"
                            401 -> "Błędna autentykacja"
                            429 -> "Zbyt wiele zapytań - spróbuj później"
                            else -> "Błąd API: ${response.message()}"
                        }

                        Toast.makeText(
                            requireContext(),
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()

                        Log.e(
                            "API",
                            "Błąd odpowiedzi: ${response.code()} - ${response.message()}"
                        )
                    }
                }

                override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Błąd sieci: ${t.message ?: "Nieznany błąd"}",
                        Toast.LENGTH_LONG
                    ).show()

                    Log.e(
                        "API",
                        "Wykonanie zapytania zakończone niepowodzeniem: ${t.message}",
                        t
                    )
                }
            })
    }
    fun performSearch(params:SearchParameters)
    {
        var name=params.dishName

        /*runBlocking {
            val translatedText = translate(name, "pl", "en")
            name=translatedText
            Log.d("Tlumaczenie",name)
        }*/
        Toast.makeText(
            requireContext(),
            "Searching for: $name\nFilters: ${params.filters.joinToString()}\nSort by: ${params.sortOptions.joinToString()}",
            Toast.LENGTH_SHORT
        ).show()
        searchRecipesByText(name, params.filters.joinToString(",").lowercase())
        Log.d("SearchRecipes","cokolwiek: ${recipesList.size}")
        //await lub coś lepszego
        searchRecipesByText(params.dishName, params.filters.joinToString(",").lowercase(), params.sortOptions.joinToString())

    }

    val apiKey = "f73a588638a84caa8f80146a4d764e0b"
    var recipesList = mutableListOf<Recipe>()
}