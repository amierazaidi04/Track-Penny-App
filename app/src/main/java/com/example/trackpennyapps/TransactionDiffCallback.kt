package com.example.trackpennyapps

import androidx.recyclerview.widget.DiffUtil

class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        // Check if the item IDs are the same
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        // Check if the item contents are the same (for efficient updates)
        return oldItem == newItem
    }
}
