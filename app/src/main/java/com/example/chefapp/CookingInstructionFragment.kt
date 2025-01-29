package com.example.chefapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CookingInstructionFragment : Fragment() {
    private var recipeId: Int = -1
    private lateinit var instructionTextView: TextView
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button

    private var steps: List<InstructionStep> = emptyList() // Lista kroków instrukcji
    private var currentStepIndex = 0 // Indeks aktualnie wyświetlanego kroku

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipeId = it.getInt(ARG_RECIPE_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cooking_instruction, container, false)
        instructionTextView = view.findViewById(R.id.tv_instruction)
        nextButton = view.findViewById(R.id.btn_next)
        previousButton = view.findViewById(R.id.btn_previous)

        loadInstructions()

        // Obsługa przycisków
        nextButton.setOnClickListener { showNextStep() }
        previousButton.setOnClickListener { showPreviousStep() }

        return view
    }

    private fun loadInstructions() {
        if (recipeId == -1) {
            instructionTextView.text = "Błąd: nieprawidłowe ID przepisu"
            return
        }

        RetrofitInstance.api.getAnalyzedInstructions(
            recipeId,
            "26a6b66669354a3fab1a34af17b17baf"
        ).enqueue(object : Callback<List<AnalyzedInstruction>> {
            override fun onResponse(
                call: Call<List<AnalyzedInstruction>>,
                response: Response<List<AnalyzedInstruction>>
            ) {
                if (response.isSuccessful) {
                    val instructions = response.body()
                    steps = instructions?.flatMap { it.steps } ?: emptyList()

                    if (steps.isNotEmpty()) {
                        currentStepIndex = 0
                        updateStepText()
                    } else {
                        instructionTextView.text = "Brak dostępnych instrukcji"
                    }
                } else {
                    instructionTextView.text = "Błąd: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<List<AnalyzedInstruction>>, t: Throwable) {
                instructionTextView.text = "Błąd sieci: ${t.message}"
            }
        })
    }

    private fun updateStepText() {
        if (steps.isNotEmpty() && currentStepIndex in steps.indices) {
            val step = steps[currentStepIndex]
            instructionTextView.text = "Krok ${step.number}: ${step.step}"
        }

        // Aktualizacja stanu przycisków
        previousButton.isEnabled = currentStepIndex > 0
        nextButton.isEnabled = currentStepIndex < steps.size - 1
    }

    private fun showNextStep() {
        if (currentStepIndex < steps.size - 1) {
            currentStepIndex++
            updateStepText()
        }
    }

    private fun showPreviousStep() {
        if (currentStepIndex > 0) {
            currentStepIndex--
            updateStepText()
        }
    }

    companion object {
        private const val ARG_RECIPE_ID = "recipe_id"

        fun newInstance(recipeId: Int) =
            CookingInstructionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_RECIPE_ID, recipeId)
                }
            }
    }
}
