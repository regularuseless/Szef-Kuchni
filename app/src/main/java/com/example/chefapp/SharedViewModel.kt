package com.example.chefapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val selectedAllergens = MutableLiveData<Set<String>>()
}