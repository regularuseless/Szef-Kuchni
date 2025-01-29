package com.example.chefapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class CookingInstructionFragment : Fragment() {
    private var param1: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cooking_instruction, container, false)

        val instructionTextView: TextView = view.findViewById(R.id.tv_instruction)
        instructionTextView.text = "Start cooking" // Wyświetlenie tytułu przepisu

        return view
    }

}