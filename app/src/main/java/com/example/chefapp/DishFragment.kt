package com.example.chefapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DishFragment : Fragment() {
    private lateinit var  sharedPreferences: SharedPreferences
    private var streakCount: Int = 0
    private var lastCookingDate: String = ""
    private val detailedRecipeViewModel: DetailedRecipeViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private var recipe: Recipe? = null
    private lateinit var favoritesManager: FavoritesManager
    private lateinit var favButton: Button
    private lateinit var delButton: Button
    private lateinit var printButton: Button

    private inner class FavoritesManager(context: Context) {
        private val fileName = "favorites.json"
        private val gson = Gson()
        private val context: Context = context.applicationContext

        fun addFavorite(recipeId: Int) {
            val current = loadFavorites().toMutableList()
            if (!current.contains(recipeId)) {
                current.add(recipeId)
                saveFavorites(current)
            }
        }

        fun removeFavorite(recipeId: Int) {
            val current = loadFavorites().toMutableList()
            if (current.remove(recipeId)) {
                saveFavorites(current)
            }
        }

        private fun saveFavorites(recipeIds: List<Int>) {
            try {
                val jsonString = gson.toJson(recipeIds)
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                    it.write(jsonString.toByteArray())
                }
            } catch (e: Exception) {
                Log.e("FavoritesManager", "Błąd zapisu: ${e.message}")
            }
        }

        fun loadFavorites(): List<Int> {
            return try {
                val inputStream = context.openFileInput(fileName)
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                gson.fromJson(jsonString, Array<Int>::class.java)?.toList() ?: emptyList()
            } catch (e: Exception) {
                Log.e("FavoritesManager", "Błąd odczytu: ${e.message}")
                emptyList()
            }
        }

        fun isFavorite(recipeId: Int): Boolean {
            return loadFavorites().contains(recipeId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dish, container, false)
        favoritesManager = FavoritesManager(requireContext())

        sharedPreferences = requireContext().getSharedPreferences("CookingStreak",Context.MODE_PRIVATE)

        loadStreakData()



        val dishImageView: ImageView = view.findViewById(R.id.iv_dish_image)
        val dishNameTextView: TextView = view.findViewById(R.id.tv_dish_name)
        val startCookingButton: Button = view.findViewById(R.id.btn_start_cooking)
        val dishCostTextView: TextView = view.findViewById(R.id.tv_dish_cost)
        val dishDifficultyTextView: TextView = view.findViewById(R.id.tv_dish_difficulty)
        val dishCaloriesTextView: TextView = view.findViewById(R.id.tv_dish_calories)
        val dishDescriptionTextView: TextView = view.findViewById(R.id.tv_dish_description)
        val dishDietTextView: TextView = view.findViewById(R.id.tv_diet_type)
        val btnAddAllIngredients: Button = view.findViewById(R.id.btn_add_to_cart)
        val ingredientsListView: ListView = view.findViewById(R.id.lv_ingredients)
        favButton = view.findViewById(R.id.fav)
        delButton = view.findViewById(R.id.del)
        printButton = view.findViewById(R.id.print)

        printButton.setOnClickListener {
            recipe?.let { generatePdf(it) } ?: showError("Brak danych przepisu")
        }

        favButton.setOnClickListener {
            recipe?.id?.let { recipeId ->
                favoritesManager.addFavorite(recipeId)
                showToast("Dodano do ulubionych!")
                updateFavoriteButtons()
            } ?: showError("Brak ID przepisu")
        }

        delButton.setOnClickListener {
            recipe?.id?.let { recipeId ->
                favoritesManager.removeFavorite(recipeId)
                showToast("Usunięto z ulubionych!")
                updateFavoriteButtons()
            } ?: showError("Brak ID przepisu")
        }

        detailedRecipeViewModel.selectedRecipe.observe(viewLifecycleOwner) { selectedRecipe ->
            recipe = selectedRecipe
            updateUI(
                dishImageView,
                dishNameTextView,
                dishCostTextView,
                dishDifficultyTextView,
                dishCaloriesTextView,
                dishDescriptionTextView,
                dishDietTextView,
                ingredientsListView
            )
            updateFavoriteButtons()
        }

        btnAddAllIngredients.setOnClickListener {
            recipe?.extendedIngredients?.let { ingredients ->
                cartViewModel.addIngredients(ingredients)
                showToast("Dodano składniki do koszyka")
            }
        }

        startCookingButton.setOnClickListener {
            updateStreakCount()
            openCookingInstructionFragment()
        }

        return view
    }
    private fun loadStreakData() {
        streakCount = sharedPreferences.getInt("streakCount", 0)
        lastCookingDate = sharedPreferences.getString("lastCookingDate", "") ?: ""
    }

    private fun updateStreakCount() {
        val currentDate = getCurrentDate()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        if (lastCookingDate.isEmpty()) {
            // First cooking session: Start the streak
            streakCount = 1
        } else {
            val lastCooking = dateFormat.parse(lastCookingDate)
            val current = dateFormat.parse(currentDate)

            if (lastCooking != null && current != null) {
                val diffInMillis = current.time - lastCooking.time
                val diffInHours = diffInMillis / (1000 * 60 * 60)

                if (diffInHours <= 48) {
                    // User cooked within 48 hours: Increment the streak
                    streakCount++
                } else {
                    // User cooked after 48 hours: Reset the streak
                    streakCount = 1
                }
            }
        }

        // Save the updated streak count and last cooking date
        saveStreakData(currentDate)

        // Notify the user about the updated streak
        showToast("Cooking streak: $streakCount")
    }

    private fun saveStreakData(currentDate: String) {
        val editor = sharedPreferences.edit()
        editor.putInt("streakCount", streakCount)
        editor.putString("lastCookingDate", currentDate)
        editor.apply()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    private fun generatePdf(recipe: Recipe) {
        RetrofitInstance.api.getRecipeDetails(
            recipe.id,
            true,
            "f73a588638a84caa8f80146a4d764e0b"
        ).enqueue(object : Callback<RecipeDetails> {
            override fun onResponse(
                call: Call<RecipeDetails>,
                response: Response<RecipeDetails>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { details ->
                        createPdfDocument(details)
                    }
                } else {
                    showError("Błąd pobierania instrukcji")
                }
            }

            override fun onFailure(call: Call<RecipeDetails>, t: Throwable) {
                showError("Błąd sieci: ${t.message}")
            }
        })
    }

    private fun createPdfDocument(recipeDetails: RecipeDetails) {
        try {
            val document = PdfDocument()
            var currentPageNumber = 1
            var page: PdfDocument.Page? = null
            var canvas: Canvas? = null
            var yPos = 80f
            val margin = 50f
            val pageWidth = 595f
            val pageHeight = 842f
            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
            }
            val lineHeight = textPaint.fontSpacing
            var isFirstPage = true
            var globalStepNumber = 1

            fun startNewPage() {
                page?.let { document.finishPage(it) }
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth.toInt(), pageHeight.toInt(), currentPageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page?.canvas
                yPos = if (isFirstPage) 80f else margin
                isFirstPage = false
                currentPageNumber++
            }

            startNewPage()

            // Tytuł
            canvas?.drawText(
                recipeDetails.title,
                margin,
                yPos,
                Paint().apply {
                    color = Color.DKGRAY
                    textSize = 20f
                    isFakeBoldText = true
                }
            )
            yPos += 40f

            // Składniki
            if (yPos + lineHeight > pageHeight - margin) startNewPage()
            canvas?.drawText("Składniki:", margin, yPos, textPaint)
            yPos += 30f

            recipeDetails.extendedIngredients.forEach { ingredient ->
                val text = "- ${ingredient.name} (${ingredient.amount} ${ingredient.unit})"
                if (yPos + lineHeight > pageHeight - margin) startNewPage()
                canvas?.drawText(text, margin, yPos, textPaint)
                yPos += lineHeight
            }

            // Instrukcje
            yPos += 30f
            if (yPos + lineHeight > pageHeight - margin) startNewPage()
            canvas?.drawText("Instrukcje:", margin, yPos, textPaint)
            yPos += 30f

            recipeDetails.analyzedInstructions.flatMap { it.steps }.forEach { step ->
                val stepText = "Krok $globalStepNumber: ${step.step}"
                val lines = splitTextIntoLines(stepText, textPaint, pageWidth - 2 * margin)

                lines.forEach { line ->
                    if (yPos + lineHeight > pageHeight - margin) startNewPage()
                    canvas?.drawText(line, margin, yPos, textPaint)
                    yPos += lineHeight
                }
                yPos += 10f
                globalStepNumber++
            }

            document.finishPage(page!!)

            val docsDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "ChefApp"
            )
            docsDir.mkdirs()
            val pdfFile = File(docsDir, "${recipeDetails.title.sanitizeFileName()}.pdf")
            document.writeTo(FileOutputStream(pdfFile))
            document.close()

            showToast("PDF zapisano w: ${pdfFile.absolutePath}")
        } catch (e: Exception) {
            showError("Błąd generowania PDF: ${e.message}")
        }
    }

    private fun splitTextIntoLines(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        var currentLine = ""

        text.split(" ").forEach { word ->
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }
        lines.add(currentLine)
        return lines
    }

    private fun String.sanitizeFileName(): String {
        return this.replace("[^a-zA-Z0-9_\\-]".toRegex(), "_")
    }

    private fun updateUI(
        dishImageView: ImageView,
        dishNameTextView: TextView,
        dishCostTextView: TextView,
        dishDifficultyTextView: TextView,
        dishCaloriesTextView: TextView,
        dishDescriptionTextView: TextView,
        dishDietTextView: TextView,
        ingredientsListView: ListView
    ) {
        recipe?.let {
            dishNameTextView.text = it.title
            dishCostTextView.text = "${it.pricePerServing} ¢/porcję"
            dishDifficultyTextView.text = "${it.readyInMinutes} minut"
            dishCaloriesTextView.text = "${it.nutrition.nutrients.firstOrNull()?.amount} kcal"
            dishDescriptionTextView.text = removeHtmlTags(it.summary)
            dishDietTextView.text = it.diets.joinToString(", ").lowercase()

            ingredientsListView.adapter = ExtendedIngredientAdapter(requireContext(), it.extendedIngredients)

            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.placeholder_image)
                .into(dishImageView)
        }
    }

    private fun updateFavoriteButtons() {
        recipe?.id?.let { recipeId ->
            favButton.visibility = if (favoritesManager.isFavorite(recipeId)) View.GONE else View.VISIBLE
            delButton.visibility = if (favoritesManager.isFavorite(recipeId)) View.VISIBLE else View.GONE
        }
    }

    private fun openCookingInstructionFragment() {
        recipe?.id?.let { recipeId ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, CookingInstructionFragment.newInstance(recipeId))
                .addToBackStack(null)
                .commit()
        } ?: showError("Brak ID przepisu")
    }

    private fun removeHtmlTags(input: String): String {
        return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(context, "Błąd: $message", Toast.LENGTH_LONG).show()
    }

    private class ExtendedIngredientAdapter(
        context: Context,
        private var ingredients: List<Ingredient>
    ) : ArrayAdapter<Ingredient>(context, 0, ingredients) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.extended_item_ingredient, parent, false)

            getItem(position)?.let { ingredient ->
                view.findViewById<TextView>(R.id.tv_ingredient_name).text = ingredient.name
                view.findViewById<TextView>(R.id.tv_ingredient_amount).text =
                    "${ingredient.amount} ${ingredient.unit}"
            }

            return view
        }
    }
}