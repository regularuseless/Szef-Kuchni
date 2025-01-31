package com.example.chefapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CookingInstructionFragment : Fragment() {
    private lateinit var instructionTextView: TextView
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button

    private var steps: List<InstructionStep> = emptyList()
    private var currentStepIndex = 0
    private var recipe: Recipe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipe = it.getParcelable(ARG_RECIPE)
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

        nextButton.setOnClickListener { showNextStep() }
        previousButton.setOnClickListener { showPreviousStep() }

        return view
    }

    private fun loadInstructions() {
        recipe?.let {
            if (it.sourceName == "Custom") {
                steps = it.analyzedInstructions.flatMap { instruction -> instruction.steps }
                if (steps.isNotEmpty()) {
                    currentStepIndex = 0
                    updateStepText()
                } else {
                    Log.d("Instrukcje","Brak dostępnych instrukcji")
                }
            } else {
                RetrofitInstance.api.getAnalyzedInstructions(
                    it.id,
                    "f73a588638a84caa8f80146a4d764e0b"
                ).enqueue(object : Callback<List<AnalyzedInstruction>> {
                    override fun onResponse(
                        call: Call<List<AnalyzedInstruction>>,
                        response: Response<List<AnalyzedInstruction>>
                    ) {
                        if (response.isSuccessful) {
                            steps = response.body()?.flatMap { it.steps } ?: emptyList()
                            if (steps.isNotEmpty()) {
                                currentStepIndex = 0
                                updateStepText()
                            } else {
                                Log.d("Instrukcje","Brak dostępnych instrukcji")
                            }
                        } else {
                            Log.d("Instrukcje","Błąd API: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<List<AnalyzedInstruction>>, t: Throwable) {
                        Log.d("Instrukcje","Błąd sieci: ${t.message}")
                    }
                })
            }
        } ?: Log.d("Instrukcje","Brak przepisu")
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

    private fun updateStepText() {
        instructionTextView.text = if (steps.isNotEmpty() && currentStepIndex in steps.indices) {
            val step = steps[currentStepIndex]
            "Krok ${step.number}: ${step.step}"
        } else {
            "Brak instrukcji do wyświetlenia"
        }
        updateButtonStates()
    }

    private fun updateButtonStates() {
        previousButton.isEnabled = currentStepIndex > 0 && steps.isNotEmpty()
        nextButton.isEnabled = currentStepIndex < steps.size - 1 && steps.isNotEmpty()
    }

    companion object {
        private const val ARG_RECIPE = "recipe"

        fun newInstance(recipe: Recipe) =
            CookingInstructionFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_RECIPE, recipe)
                }
            }
    }
}