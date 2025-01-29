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
import kotlinx.coroutines.withContext

class SearchListFragment : Fragment() {
    private val searchViewModel: SearchViewModel by activityViewModels()
    private lateinit var recipeAdapter: RecipeAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container:ViewGroup?,
        SavedInstanceState:Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_search_list,container,false)
        val recyclerView:RecyclerView = view.findViewById(R.id.rv_dishes)
        searchViewModel.searchParameters.observe(viewLifecycleOwner)
        {
            params -> performSearch(params)
        }
        recipeAdapter = RecipeAdapter(recipesList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recipeAdapter
        return view
    }
    fun searchRecipesByText(query: String, intolerances: String? = null, sort: String? = null) {
        RetrofitInstance.api.searchRecipesByText(query, intolerances, sort, apiKey = apiKey).enqueue(object : Callback<RecipeSearchResponse> {
            override fun onResponse(call: Call<RecipeSearchResponse>, response: Response<RecipeSearchResponse>) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.results ?: emptyList()  // Pobierz przepisy
                    recipesList.clear()
                    recipesList.addAll(recipes)
                    recipeAdapter.notifyDataSetChanged()
                    Log.d("SearchRecipes","cokolwiek: ${recipesList.size}")
                } else {
                    Log.e("API", "Błąd odpowiedzi: ${response.message()}")  // Logowanie błędu odpowiedzi
                }
            }
            override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                Log.e("API", "Wykonanie zapytania zakończone niepowodzeniem: ${t.message}")
            }
        })
    }
    private fun performSearch(params:SearchParameters)
    {
        Toast.makeText(
            requireContext(),
            "Searching for: $params.dishName\nFilters: ${params.filters.joinToString()}\nSort by: ${params.sortOptions.joinToString()}",
            Toast.LENGTH_SHORT
        ).show()
        searchRecipesByText(params.dishName, params.filters.joinToString(",").lowercase())
        Log.d("SearchRecipes","cokolwiek: ${recipesList.size}")
        //await lub coś lepszego

    }

    val apiKey = "c00df3c343d14c7390f49b9adc0c1cfe"
    var recipesList = mutableListOf<Recipe>()


}