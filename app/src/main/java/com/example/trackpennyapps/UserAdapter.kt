package com.example.trackpennyapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private var userList: List<Model>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.nameTextView.text = currentUser.userName
        holder.emailTextView.text = currentUser.userEmail
        holder.passwordTextView.text = currentUser.userPassword

        holder.deleteButton.setOnClickListener {
            listener.onDeleteClick(position)
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        val passwordTextView: TextView = itemView.findViewById(R.id.passwordTextView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    // Add filter function to filter the list based on query
    fun filter(query: String?) {
        val filteredList = mutableListOf<Model>()
        if (!query.isNullOrEmpty()) {
            for (item in userList) {
                if ((item.userName?.contains(query, ignoreCase = true) == true) ||
                    (item.userEmail?.contains(query, ignoreCase = true) == true) ||
                    (item.userPassword?.contains(query, ignoreCase = true) == true)) {
                    filteredList.add(item)
                }
            }
        } else {
            filteredList.addAll(userList)
        }
        // Update userList with filtered data
        userList = filteredList
        notifyDataSetChanged()
    }
}
