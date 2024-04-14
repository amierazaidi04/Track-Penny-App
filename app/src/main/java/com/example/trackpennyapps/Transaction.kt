package com.example.trackpennyapps

class Transaction(
    val id: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val description: String = "",
    val isBudget: Boolean = false
)