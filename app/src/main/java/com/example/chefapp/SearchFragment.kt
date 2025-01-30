package com.example.chefapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
class SearchFragment : Fragment() {

    private lateinit var searchBar: EditText
    private lateinit var filterVegan: CheckBox
    private lateinit var filterVegetarian: CheckBox
    private lateinit var filterGlutenFree: CheckBox
    private lateinit var filterNutsFree: CheckBox
    private lateinit var filterDairyFree: CheckBox
    private lateinit var sortOptionsGroup: RadioGroup
    private lateinit var searchButton: Button

    private val searchViewModel: SearchViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels() // Dodaj SharedViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialize Views
        searchBar = view.findViewById(R.id.searchBar)
        //filterVegan = view.findViewById(R.id.filterVegan)
        //filterVegetarian = view.findViewById(R.id.filterVegetarian)
        filterGlutenFree = view.findViewById(R.id.filterGlutenFree)
        filterNutsFree = view.findViewById(R.id.filterNutsFree)
        filterDairyFree = view.findViewById(R.id.filterDairyFree)
        sortOptionsGroup = view.findViewById(R.id.sortOptionsGroup)
        searchButton = view.findViewById(R.id.btnSearch)

        // Przywróć zaznaczone alergeny z SharedViewModel
        sharedViewModel.selectedAllergens.observe(viewLifecycleOwner) { selectedAllergens ->
            if (selectedAllergens != null) {
                filterGlutenFree.isChecked = selectedAllergens.contains("Gluten")
                filterNutsFree.isChecked = selectedAllergens.contains("Nuts")
                filterDairyFree.isChecked = selectedAllergens.contains("Dairy")
            }
        }

        setupSearchButton()

        return view
    }

    private fun setupSearchButton() {
        searchButton.setOnClickListener {
            val dishName = searchBar.text.toString()

            // Get selected filters (checkboxes)
            val selectedFilters = mutableListOf<String>()
            //if (filterVegan.isChecked) selectedFilters.add("Vegan")
            //if (filterVegetarian.isChecked) selectedFilters.add("Vegetarian")
            if (filterGlutenFree.isChecked) selectedFilters.add("Gluten")
            if (filterNutsFree.isChecked) selectedFilters.add("Nuts")
            if (filterDairyFree.isChecked) selectedFilters.add("Dairy")

            // Get selected sort options (radio buttons)
            val selectedSortOptions = mutableListOf<String>()
            for (i in 0 until sortOptionsGroup.childCount) {
                val radioButton = sortOptionsGroup.getChildAt(i) as RadioButton
                if (radioButton.isChecked) {
                    selectedSortOptions.add(radioButton.text.toString())
                }
            }

            // Trigger the search action
            //performTestSearch(dishName, selectedFilters, selectedSortOptions)
            val searchParams = SearchParameters(dishName, selectedFilters, selectedSortOptions)
            searchViewModel.setSearchParameters(searchParams)

            // Navigate to SearchListFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, SearchListFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun performTestSearch(dishName: String, filters: List<String>, sortOptions: List<String>) {
        // here we perform the search action
        Toast.makeText(
            requireContext(),
            "Searching for: $dishName\nFilters: ${filters.joinToString()}\nSort by: ${sortOptions.joinToString()}",
            Toast.LENGTH_SHORT
        ).show()
    }
}
