package com.example.trackpennyapps

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference

    private lateinit var updateTransactionBtn: Button
    private lateinit var closeBtn: ImageButton
    private lateinit var categorySpinner: Spinner
    private lateinit var amountInput: EditText
    private lateinit var dateButton: Button
    private lateinit var descriptionInput: EditText

    private lateinit var transactionId: String // Added for tracking transaction ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        transactionId = intent.getStringExtra("transactionId") ?: ""

        updateTransactionBtn = findViewById(R.id.updateTransactionBtn)
        closeBtn = findViewById(R.id.closeBtn)
        categorySpinner = findViewById(R.id.categorySpinner)
        amountInput = findViewById(R.id.amountInput)
        dateButton = findViewById(R.id.dateButton)
        descriptionInput = findViewById(R.id.descriptionInput)

        val categories = resources.getStringArray(R.array.categories_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        val category = intent.getStringExtra("category") ?: ""
        val amount = intent.getDoubleExtra("amount", 0.0)
        val date = intent.getStringExtra("date") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        categorySpinner.setSelection(categories.indexOf(category))
        amountInput.setText(amount.toString())
        dateButton.text = date
        descriptionInput.setText(description)

        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        updateTransactionBtn.setOnClickListener {
            val selectedCategory = categorySpinner.selectedItem.toString()
            val amount = amountInput.text.toString()
            val date = dateButton.text.toString()
            val description = descriptionInput.text.toString()

            updateData(selectedCategory, amount, date, description)
        }

        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                dateButton.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateData(category: String, amount: String, date: String, description: String) {
        try {
            val amountValue = amount.toDoubleOrNull() ?: 0.0

            if (amountValue == 0.0) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_LONG).show()
                return
            }

            val updatedTransaction = Transaction(transactionId, category, amountValue, date, description)

            dbRef = FirebaseDatabase.getInstance().getReference("transactions").child(transactionId)
            dbRef.setValue(updatedTransaction)
                .addOnSuccessListener {
                    Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("EditTransactionActivity", "Failed to update transaction", e)
                    Toast.makeText(this, "Failed to update transaction", Toast.LENGTH_LONG).show()
                }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_LONG).show()
        }
    }
}