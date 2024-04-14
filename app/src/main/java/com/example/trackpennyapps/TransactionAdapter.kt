package com.example.trackpennyapps

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil

class TransactionAdapter(
    private val transactions: ArrayList<Transaction>,
    private val listener: TransactionClickListener,
    private val categories: List<String>
) : ListAdapter<Transaction, TransactionAdapter.ViewHolder>(TransactionDiffCallback()) {

    interface TransactionClickListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int, selectedCategory: String)
        fun onSaveClick(position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val amount: TextView = itemView.findViewById(R.id.amount)
        val description: TextView = itemView.findViewById(R.id.description)
        val category: TextView = itemView.findViewById(R.id.category)
        val date: TextView = itemView.findViewById(R.id.date) // Add date TextView
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        init {
            itemView.setOnClickListener(this)
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedCategory = category.text.toString()
                    listener.onDeleteClick(position, selectedCategory)
                }
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onEditClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.amount.text = transaction.amount.toCurrencyString()
        holder.amount.setColorBasedOnAmount(holder.amount.context, transaction.amount)

        holder.description.text = transaction.description
        holder.date.text = transaction.date ?: "" // Set date text, or an empty string if null
        holder.category.text = transaction.category

        // Show/hide date based on transaction position
        if (position == 0 || transactions[position - 1].date != transaction.date) {
            holder.date.visibility = View.VISIBLE
        } else {
            holder.date.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
        notifyDataSetChanged()
    }

    fun updateData(newTransactions: List<Transaction>) {
        transactions.clear()
        transactions.addAll(newTransactions)
        notifyDataSetChanged()
    }

    // Implementing DiffUtil.ItemCallback for list differencing
    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id // Assuming Transaction has an 'id' property
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}

fun Double.toCurrencyString(): String {
    return "RM%.2f".format(this)
}

fun TextView.setColorBasedOnAmount(context: Context, amount: Double) {
    val colorRes = if (amount > 0) R.color.red else R.color.green
    setTextColor(ContextCompat.getColor(context, colorRes))
}
