package com.example.chefapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import retrofit2.http.Url

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

/**
 * A simple [Fragment] subclass.
 * Use the [DishFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DishFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var recipeId: String? = null
    private var recipeTitle: String? = null
    private var recipeImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipeId = it.getString(ARG_PARAM1)
            recipeTitle = it.getString(ARG_PARAM2)
            recipeImageUrl = it.getString(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dish, container, false)
        view.findViewById<TextView>(R.id.tv_dish_name).text = recipeTitle

        // Pobierz referencję do przycisku "Start Cooking"
        val startCookingButton: Button = view.findViewById(R.id.btn_start_cooking)

        // Obsługa kliknięcia przycisku
        startCookingButton.setOnClickListener {
            openCookingInstructionFragment()
        }

        // Pobierz referencje do widoków
        val dishImageView: ImageView = view.findViewById(R.id.iv_dish_image)
        val dishNameTextView: TextView = view.findViewById(R.id.tv_dish_name)

        // Ustaw nazwę przepisu
        dishNameTextView.text = recipeTitle

        // Załaduj obraz do ImageView za pomocą Glide
        Glide.with(this)
            .load(recipeImageUrl) // URL obrazu
            .placeholder(R.drawable.placeholder_image) // Obraz zastępczy podczas ładowania
            //.error(R.drawable.error_image) // Obraz błędu
            .into(dishImageView)


        // Załaduj szczegóły przepisu według recipeId
        return view
    }

    private fun openCookingInstructionFragment() {
        val fragment = CookingInstructionFragment.newInstance(recipeId ?: "", recipeTitle ?: "")
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment) // R.id.fragment_container to kontener w Activity
            .addToBackStack(null)
            .commit()
    }

    companion object {

        fun newInstance(recipeId: String, recipeTitle: String, recipeImageUrl: String) =
            DishFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, recipeId)
                    putString(ARG_PARAM2, recipeTitle)
                    putString(ARG_PARAM3, recipeImageUrl)
                }
            }
    }
}