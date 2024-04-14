package com.example.trackpennyapps

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var transactionRecyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        transactionRecyclerView = findViewById(R.id.transactionRecyclerView)
        transactionRecyclerView.layoutManager = LinearLayoutManager(this)

        val transactionList: ArrayList<Transaction> = arrayListOf() // Initialize with an empty list
        val categories: ArrayList<String> = arrayListOf() // Initialize with an empty list

        val listener = object : TransactionAdapter.TransactionClickListener {
            override fun onEditClick(position: Int) {
                // Implement edit logic
            }

            override fun onDeleteClick(position: Int, selectedCategory: String) {
                // Implement delete logic
            }

            override fun onSaveClick(position: Int) {
                // Implement save logic
            }
        }

        transactionAdapter = TransactionAdapter(transactionList, listener, categories)
        transactionRecyclerView.adapter = transactionAdapter

        // Add Transaction Button
        findViewById<View>(R.id.addTransactionBtn).setOnClickListener {
            // Implement your logic for adding a new transaction
        }
    }
}
