package com.example.trackpennyapps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // Define a MutableLiveData to hold the budget amount
    val budgetAmount = MutableLiveData<Double>()

    // Define a MutableLiveData to hold the user ID
    val userId = MutableLiveData<String>()

    // Define a method to set the budget amount
    fun setBudgetAmount(amount: Double) {
        budgetAmount.value = amount
    }

    // Define a method to set the user ID
    fun setUserId(id: String) {
        userId.value = id
    }
}
