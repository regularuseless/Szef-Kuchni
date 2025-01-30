package com.example.chefapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

class CartFragment : Fragment() {
    private val cartViewModel: CartViewModel by activityViewModels()
    //private val cartIngredients = mutableListOf<Ingredient>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IngredientAdapter
    private val ingredients = mutableListOf<Ingredient>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        // Przywróć zapisane składniki
        val savedIngredients = loadIngredientsFromJson()
        if (savedIngredients.isNotEmpty()) {
            cartViewModel.addIngredients(savedIngredients)
        }

        // Obserwuj zmiany w liście składników
        cartViewModel.ingredients.observe(viewLifecycleOwner) { newIngredients ->
            if (newIngredients != null) {
                ingredients.clear()
                ingredients.addAll(newIngredients)
                adapter.notifyDataSetChanged()
            }
        }

        setupRecyclerView()

        // Dodaj przycisk zapisu (możesz go dodać w XML lub programowo)
        view.findViewById<Button>(R.id.btn_save_cart).setOnClickListener {
            saveIngredientsToJson()
        }

        // Dodaj przycisk czyszczenia koszyka
        view.findViewById<Button>(R.id.btn_clear_cart).setOnClickListener {
            clearCart()
        }

        return view
    }

    private fun setupRecyclerView() {
        adapter = IngredientAdapter(ingredients) { updatedIngredient ->
            // Aktualizuj ilość składnika w ViewModel
            cartViewModel.updateIngredient(updatedIngredient)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun saveIngredientsToJson() {
        val gson = Gson()
        val json = gson.toJson(ingredients) // Konwertuj listę składników do JSON

        // Zapisz do pliku
        val file = File(requireContext().filesDir, "cart_ingredients.json")
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }

        // Powiadom użytkownika
        android.widget.Toast.makeText(requireContext(), "Cart saved to JSON!", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun loadIngredientsFromJson(): List<Ingredient> {
        val file = File(requireContext().filesDir, "cart_ingredients.json")
        return if (file.exists()) {
            val json = file.readText()
            val gson = Gson()
            gson.fromJson(json, Array<Ingredient>::class.java).toList()
        } else {
            emptyList()
        }
    }
    private fun clearCart() {
        // Wyczyść listę składników w ViewModel
        cartViewModel.clearCart()

        // Usuń plik JSON
        val file = File(requireContext().filesDir, "cart_ingredients.json")
        if (file.exists()) {
            file.delete()
        }

        // Powiadom użytkownika
        android.widget.Toast.makeText(requireContext(), "Cart cleared!", android.widget.Toast.LENGTH_SHORT).show()
    }
}
