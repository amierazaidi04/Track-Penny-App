package com.example.trackpennyapps

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminMainActivity : AppCompatActivity(), UserAdapter.OnItemClickListener {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userList: MutableList<Model>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("user")
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userList = mutableListOf()

        // Set up RecyclerView
        userAdapter = UserAdapter(userList, this)
        userRecyclerView.adapter = userAdapter

        // Fetch user data from database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(Model::class.java)
                    user?.let {
                        userList.add(user)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })

        // Add User Button
        findViewById<View>(R.id.addUserBtn).setOnClickListener {
            startActivity(Intent(this@AdminMainActivity, AddUserActivity::class.java))
        }
    }

    override fun onItemClick(position: Int) {
        val selectedUser = userList[position]
        // Pass user data to UpdateUserActivity
        val intent = Intent(this@AdminMainActivity, UpdateUserActivity::class.java).apply {
            putExtra("userId", selectedUser.userId) // Pass user id to identify the user in UpdateUserActivity
            putExtra("userName", selectedUser.userName)
            putExtra("userEmail", selectedUser.userEmail)
            putExtra("userPassword", selectedUser.userPassword)
            // Add other user data if needed
        }
        startActivity(intent)
    }

    override fun onDeleteClick(position: Int) {
        val selectedUser = userList[position]
        val userId = selectedUser.userId ?: ""
        databaseReference.child(userId).removeValue()
            .addOnSuccessListener {
                // User deleted successfully
                Toast.makeText(
                    this@AdminMainActivity,
                    "User deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                // Failed to delete user
                Toast.makeText(this@AdminMainActivity, "Failed to delete user", Toast.LENGTH_SHORT)
                    .show()
            }
    }

}