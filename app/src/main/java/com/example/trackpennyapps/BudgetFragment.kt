package com.example.trackpennyapps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.util.Log

class BudgetFragment : Fragment() {

    private val TAG = "BudgetFragment"
    private lateinit var amountEditText: EditText
    private lateinit var addButton: Button
    private lateinit var databaseRef: DatabaseReference
    private lateinit var userId: String // Add this variable to store the user ID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        amountEditText = view.findViewById(R.id.amountEditText)
        addButton = view.findViewById(R.id.addButton)

        // Initialize DatabaseReference
        databaseRef = FirebaseDatabase.getInstance().getReference("budgets")

        // Retrieve user ID from arguments or wherever it is stored
        userId = arguments?.getString("userId") ?: ""

        addButton.setOnClickListener {
            val amountText = amountEditText.text.toString()
            if (amountText.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0.0) {
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save budget to database
            val budgetId = databaseRef.push().key ?: ""
            val budgetTransaction = Budget(budgetId, amount)

            databaseRef.child(budgetId).setValue(budgetTransaction)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Notify the user that the budget was added successfully
                        Toast.makeText(requireContext(), "Budget added successfully", Toast.LENGTH_SHORT).show()
                        // Clear the EditText after adding the budget
                        amountEditText.text = null
                    } else {
                        // Notify the user of failure
                        Toast.makeText(requireContext(), "Failed to add budget", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Handle any exceptions that occur during the database operation
                    Log.e(TAG, "Error adding budget: ${e.message}")
                    // Notify the user or take appropriate action
                    Toast.makeText(requireContext(), "Failed to add budget: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        return view
    }
}
