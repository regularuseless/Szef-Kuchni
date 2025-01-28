package com.example.chefapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IngredientAdapter
    private val ingredients = mutableListOf(
        Ingredient_test("Mleko", 1),
        Ingredient_test("Papryka", 2),
        Ingredient_test("Makaron spaghetti", 0),
        Ingredient_test("Kakao", 1)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        setupRecyclerView()

        return view
    }

    private fun setupRecyclerView() {
        adapter = IngredientAdapter(ingredients) { updatedIngredient ->
            // Handle quantity changes if needed
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }
}
