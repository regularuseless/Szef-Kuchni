package com.example.chefapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    private val _searchParameters = MutableLiveData<SearchParameters>()
    val searchParameters: LiveData<SearchParameters> get() = _searchParameters

    fun setSearchParameters(params: SearchParameters) {
        _searchParameters.value = params
    }
}

data class SearchParameters(
    val dishName: String,
    val filters: List<String>,
    val sortOptions: List<String>
)
