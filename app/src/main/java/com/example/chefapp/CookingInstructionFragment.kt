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
    private var recipeId: Int = -1
    private lateinit var instructionTextView: TextView
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button

    private var steps: List<InstructionStep> = emptyList()
    private var currentStepIndex = 0

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

        nextButton.setOnClickListener { showNextStep() }
        previousButton.setOnClickListener { showPreviousStep() }

        return view
    }

    private fun loadInstructions() {
        if (recipeId == -1) {
            showError("Nieprawidłowe ID przepisu")
            return
        }

        RetrofitInstance.api.getAnalyzedInstructions(
            recipeId,
            "f73a588638a84caa8f80146a4d764e0b" // Uważaj na ekspozycję klucza w kodzie!
        ).enqueue(object : Callback<List<AnalyzedInstruction>> {
            override fun onResponse(
                call: Call<List<AnalyzedInstruction>>,
                response: Response<List<AnalyzedInstruction>>
            ) {
                if (response.isSuccessful) {
                    handleSuccessfulResponse(response)
                } else {
                    handleErrorResponse(response, call)
                }
            }

            override fun onFailure(call: Call<List<AnalyzedInstruction>>, t: Throwable) {
                handleNetworkFailure(t, call)
            }
        })
    }

    private fun handleSuccessfulResponse(response: Response<List<AnalyzedInstruction>>) {
        val instructions = response.body()
        steps = instructions?.flatMap { it.steps } ?: emptyList()

        if (steps.isNotEmpty()) {
            currentStepIndex = 0
            updateStepText()
        } else {
            showInformation("Brak dostępnych instrukcji")
        }
    }

    private fun handleErrorResponse(
        response: Response<List<AnalyzedInstruction>>,
        call: Call<List<AnalyzedInstruction>>
    ) {
        val errorMessage = when (response.code()) {
            402 -> "Koniec tokenów API - skontaktuj się z administratorem"
            401 -> "Błędna autentykacja"
            429 -> "Zbyt wiele zapytań - spróbuj później"
            else -> "Błąd API: ${response.message()}"
        }

        showError(errorMessage)
        Log.e(
            "API",
            "Błąd ${response.code()} dla URL: ${call.request().url}\nOdpowiedź: ${response.errorBody()?.string()}"
        )
        resetUI()
    }

    private fun handleNetworkFailure(t: Throwable, call: Call<List<AnalyzedInstruction>>) {
        showError("Błąd sieci: ${t.message ?: "Nieznany błąd"}")
        Log.e(
            "API",
            "Błąd sieciowy dla URL: ${call.request().url}\n${t.stackTraceToString()}"
        )
        resetUI()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        instructionTextView.text = message
    }

    private fun showInformation(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        instructionTextView.text = message
    }

    private fun resetUI() {
        steps = emptyList()
        updateStepText()
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