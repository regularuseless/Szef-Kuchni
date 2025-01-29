package com.example.chefapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Odczytaj zapisane alergeny
        val selectedAllergens = loadSelectedAllergens()

        // Znajdź LinearLayout, w którym będą wyświetlane alergeny
        val llAllergensList = view.findViewById<LinearLayout>(R.id.ll_allergens_list)

        // Wyczyść istniejące widoki (na wypadek, gdyby były już jakieś elementy)
        llAllergensList.removeAllViews()

        // Dodaj każdy alergen do LinearLayout
        for (allergen in selectedAllergens) {
            val textView = TextView(requireContext()).apply {
                text = allergen
                textSize = 16f
                setPadding(0, 8, 0, 8) // Dodaj odstępy między elementami
            }
            llAllergensList.addView(textView)
        }


        // Odczytaj zapisane preferencje dietetyczne
        val selectedDiets = loadSelectedDiets()
        val llDietsList = view.findViewById<LinearLayout>(R.id.ll_diets_list)
        llDietsList.removeAllViews()
        for (diet in selectedDiets) {
            val textView = TextView(requireContext()).apply {
                text = diet
                textSize = 16f
                setPadding(0, 8, 0, 8)
            }
            llDietsList.addView(textView)
        }

        return view
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}