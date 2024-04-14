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

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference

    private lateinit var addTransactionBtn: Button
    private lateinit var closeBtn: ImageButton
    private lateinit var categorySpinner: Spinner
    private lateinit var amountInput: EditText
    private lateinit var dateButton: Button
    private lateinit var descriptionInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        addTransactionBtn = findViewById(R.id.addTransactionBtn)
        closeBtn = findViewById(R.id.closeBtn)
        categorySpinner = findViewById(R.id.categorySpinner)
        amountInput = findViewById(R.id.amountInput)
        dateButton = findViewById(R.id.dateButton)
        descriptionInput = findViewById(R.id.descriptionInput)

        // Populate spinner with categories from string array
        val categories = resources.getStringArray(R.array.categories_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Set OnClickListener to show DatePickerDialog
        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        addTransactionBtn.setOnClickListener {
            val selectedCategory = categorySpinner.selectedItem.toString()
            val amount = amountInput.text.toString()
            val date = dateButton.text.toString()
            val description = descriptionInput.text.toString()

            saveData(selectedCategory, amount, date, description)
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

                // Update the text of the button with the selected date
                dateButton.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun saveData(category: String, amount: String, date: String, description: String) {
        try {
            val amountValue = amount.toDoubleOrNull() ?: 0.0

            if (amountValue == 0.0) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_LONG).show()
                return
            }

            dbRef = FirebaseDatabase.getInstance().getReference("transactions")
            val transactionId = dbRef.push().key ?: ""

            val transaction = Transaction(transactionId, category, amountValue, date, description)

            dbRef.child(transactionId).setValue(transaction)
                .addOnSuccessListener {
                    Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("AddTransactionActivity", "Failed to add transaction", e)
                    Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_LONG).show()
                }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_LONG).show()
        }
    }
}