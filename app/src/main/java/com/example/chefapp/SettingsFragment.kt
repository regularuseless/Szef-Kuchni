package com.example.chefapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream


class SettingsFragment : Fragment() {

    private lateinit var allergenAdapter: AllergenAdapter
    private lateinit var dietAdapter: DietAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Inicjalizacja RecyclerView dla alergenów
        val rvAllergens = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_allergens)
        rvAllergens.layoutManager = LinearLayoutManager(requireContext())

        // Lista alergenów
        val allergens = listOf("Gluten", "Nuts", "Dairy")

        // Inicjalizacja adaptera
        allergenAdapter = AllergenAdapter(allergens)
        rvAllergens.adapter = allergenAdapter

        // Przywróć zapisane alergeny
        val savedAllergens = loadSelectedAllergens()
        allergenAdapter.setSelectedAllergens(savedAllergens)


        ///////


        // Inicjalizacja RecyclerView dla preferencji dietetycznych
        val rvDiets = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_diet)
        rvDiets.layoutManager = LinearLayoutManager(requireContext())

        // Lista preferencji dietetycznych
        val diets = listOf("Vegetarian", "Gluten Free", "Ketogenic", "Lacto-Vegetarian", "Ovo-Vegetarian", "Vegan", "Pescetarian", "Paleo", "Primal")

        // Inicjalizacja adaptera
        dietAdapter = DietAdapter(diets)
        rvDiets.adapter = dietAdapter


        //Przywróć zapisane diety
        val savedDiets = loadSelectedDiets()
        dietAdapter.setSelectedDiets(savedDiets)




        // Przycisk zapisu
        val btnSave = view.findViewById<android.widget.Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            saveSelectedAllergens()
            saveSelectedDiets()
        }

        return view
    }

    private fun saveSelectedAllergens() {
        // Pobierz wybrane alergeny z adaptera
        val selectedAllergens = allergenAdapter.getSelectedAllergens()

        // Konwersja do JSON
        val gson = Gson()
        val json = gson.toJson(selectedAllergens)

        // Zapis do pliku
        val file = File(requireContext().filesDir, "allergens.json")
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }
        // Możesz dodać powiadomienie dla użytkownika, że dane zostały zapisane
        android.widget.Toast.makeText(requireContext(), "Allergens saved!", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun loadSelectedAllergens(): Set<String> {
        val file = File(requireContext().filesDir, "allergens.json")
        return if (file.exists()) {
            val json = file.readText()
            val gson = Gson()
            gson.fromJson(json, Set::class.java) as Set<String>
        } else {
            emptySet()
        }
    }

    private fun saveSelectedDiets() {
        val selectedDiets = dietAdapter.getSelectedDiets()
        val gson = Gson()
        val json = gson.toJson(selectedDiets)
        val file = File(requireContext().filesDir, "diets.json")
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }

    }


    private fun loadSelectedDiets(): Set<String> {
        val file = File(requireContext().filesDir, "diets.json")
        return if (file.exists()) {
            val json = file.readText()
            val gson = Gson()
            gson.fromJson(json, Set::class.java) as Set<String>
        } else {
            emptySet()
        }
    }
}