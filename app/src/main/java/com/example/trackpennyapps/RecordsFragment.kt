package com.example.trackpennyapps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.google.firebase.database.*

class RecordsFragment : Fragment(), TransactionAdapter.TransactionClickListener {

    private lateinit var balance: TextView
    private lateinit var budget: TextView
    private lateinit var expense: TextView
    private lateinit var addBtn: FloatingActionButton
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var originalTransactions: ArrayList<Transaction>
    private lateinit var budgetTransactions: ArrayList<Transaction>
    private lateinit var expenseTransactions: ArrayList<Transaction>
    private lateinit var recyclerview: RecyclerView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_records, container, false)

        // Initialize views
        balance = view.findViewById(R.id.balance)
        budget = view.findViewById(R.id.budget) as TextView
        expense = view.findViewById(R.id.expense)
        addBtn = view.findViewById(R.id.addBtn)
        searchView = view.findViewById(R.id.searchView)
        recyclerview = view.findViewById(R.id.recyclerview)

        // Sample list of categories (replace with your actual list)
        val categories = listOf("Category 1", "Category 2", "Category 3")

        // Initialize data structures and adapter
        originalTransactions = ArrayList()
        budgetTransactions = ArrayList()
        expenseTransactions = ArrayList()
        transactionAdapter = TransactionAdapter(expenseTransactions, this, categories)
        recyclerview.adapter = transactionAdapter
        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        // Get reference to the Firebase database
        databaseRef = FirebaseDatabase.getInstance().getReference("transactions")
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Load transactions from database
        loadTransactionsFromDatabase()

        // Set up search view
        setUpSearchView()

        // Set click listener for add button
        addBtn.setOnClickListener {
            val intent = Intent(requireContext(), AddTransactionActivity::class.java)
            startActivity(intent)
        }

        // Listen for changes in the budget data
        val budgetRef = FirebaseDatabase.getInstance().getReference("budgets")
        budgetRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalBudget = 0.0
                for (budgetSnapshot in snapshot.children) {
                    val budgetValue = budgetSnapshot.getValue(Transaction::class.java)
                    budgetValue?.let {
                        totalBudget += it.amount
                    }
                }
                // Update UI to display total budget
                requireActivity().runOnUiThread {
                    budget.text = "RM %.2f".format(totalBudget)
                }
                // Update the shared ViewModel with the new total budget
                sharedViewModel.setBudgetAmount(totalBudget)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        // Listen for changes in the transaction data
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                originalTransactions.clear()
                budgetTransactions.clear()
                expenseTransactions.clear()
                for (postSnapshot in snapshot.children) {
                    val transaction = postSnapshot.getValue(Transaction::class.java)
                    transaction?.let {
                        originalTransactions.add(it)
                        if (it.isBudget) {
                            budgetTransactions.add(it)
                        } else {
                            expenseTransactions.add(it)
                        }
                    }
                }
                filterTransactions(searchView.query.toString())
                updateDashboard()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        // Observe changes to the budget amount in the shared ViewModel
        sharedViewModel.budgetAmount.observe(viewLifecycleOwner) { amount ->
            budget.text = "RM %.2f".format(amount)
            updateDashboard()
        }

        return view
    }

    private fun loadTransactionsFromDatabase() {
        // Load transactions from database and populate the originalTransactions list
        // Example:
        // databaseRef.addValueEventListener(...)
    }

    private fun setUpSearchView() {
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterTransactions(newText.orEmpty())
                return true
            }
        })
    }

    private fun filterTransactions(query: String) {
        val filteredList = originalTransactions.filter { transaction ->
            transaction.category.contains(query, true) ||
                    transaction.description.contains(query, true) ||
                    transaction.amount.toString().contains(query, true) ||
                    transaction.date.contains(query, true)
        }.sortedByDescending { it.date } // Sort transactions by date in descending order

        transactionAdapter.submitList(filteredList)
        // Update originalTransactions with the filtered list
        expenseTransactions.clear()
        expenseTransactions.addAll(filteredList.filter { !it.isBudget })
    }

    override fun onEditClick(position: Int) {
        val intent = Intent(requireContext(), EditTransactionActivity::class.java).apply {
            putExtra("transactionId", expenseTransactions[position].id) // Assuming id is used for transaction identification
            putExtra("category", expenseTransactions[position].category)
            putExtra("amount", expenseTransactions[position].amount)
            putExtra("date", expenseTransactions[position].date)
            putExtra("description", expenseTransactions[position].description)
        }
        startActivityForResult(intent, EDIT_TRANSACTION_REQUEST_CODE)
    }

    override fun onDeleteClick(position: Int, selectedCategory: String) {
        val transactionId = expenseTransactions[position].id
        databaseRef.child(transactionId).removeValue()
            .addOnSuccessListener {
                // Transaction deleted successfully
                Snackbar.make(requireView(), "Transaction deleted successfully", LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Failed to delete transaction
                Snackbar.make(requireView(), "Failed to delete transaction", LENGTH_SHORT).show()
            }
    }

    override fun onSaveClick(position: Int) {
        // Handle save action if needed
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_TRANSACTION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val position = data?.getIntExtra("position", -1)
            val category = data?.getStringExtra("category")
            val amount = data?.getDoubleExtra("amount", 0.0)
            val description = data?.getStringExtra("description")

            if (position != null && position != -1 && category != null && amount != null && description != null
            ) {
                val transactionId = expenseTransactions[position].id
                val updatedTransaction =
                    Transaction(transactionId ?: "", category, amount, description)
                databaseRef.child(transactionId).setValue(updatedTransaction)
                    .addOnSuccessListener {
                        // Transaction updated successfully
                        Snackbar.make(requireView(), "Transaction updated successfully", LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Failed to update transaction
                        Snackbar.make(requireView(), "Failed to update transaction", LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun updateDashboard() {
        val totalBudget = sharedViewModel.budgetAmount.value ?: 0.0
        val totalExpenses = originalTransactions.filter { !it.isBudget }.sumByDouble { it.amount }

        val balanceAmount = totalBudget - totalExpenses

        balance.text = "RM %.2f".format(balanceAmount)
        budget.text = "RM %.2f".format(totalBudget)
        expense.text = "RM %.2f".format(totalExpenses)
    }

    companion object {
        const val EDIT_TRANSACTION_REQUEST_CODE = 1001
    }
}
