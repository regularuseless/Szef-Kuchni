package com.example.chefapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FridgeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IngredientAdapter
    private val fridgeIngredients = mutableListOf(
        Ingredient_test("Mleko", 2,),
        Ingredient_test("Jaja", 12,),
        Ingredient_test("Ser", 1,),
        Ingredient_test("Marchewka", 5,)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fridge, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewFridge)

        setupRecyclerView()

        return view
    }

    private fun setupRecyclerView() {
        adapter = IngredientAdapter(fridgeIngredients) { updatedIngredient ->
            // Handle quantity changes or other updates if needed
            println("Updated ingredient: ${updatedIngredient.name}, Quantity: ${updatedIngredient.quantity}")
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }
}
