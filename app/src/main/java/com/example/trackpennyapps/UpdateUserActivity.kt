package com.example.trackpennyapps

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class UpdateUserActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var updateUserBtn: Button
    private lateinit var closeBtn: ImageButton

    private lateinit var userId: String // Added for tracking user ID
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        userId = intent.getStringExtra("userId") ?: ""

        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        updateUserBtn = findViewById(R.id.updateUserBtn)
        closeBtn = findViewById(R.id.closeBtn)

        dbRef = FirebaseDatabase.getInstance().getReference("user").child(userId)

        // Retrieve user data from Firebase
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Model::class.java)
                user?.let {
                    nameInput.setText(user.userName)
                    emailInput.setText(user.userEmail)
                    passwordInput.setText(user.userPassword)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        updateUserBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            updateUserData(name, email, password)
        }

        closeBtn.setOnClickListener {
            finish() // Close the activity
        }
    }

    private fun updateUserData(name: String, email: String, password: String) {
        // Perform validation if needed
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedUser = Model(email, userId, name, password)

        // Update user data in Firebase
        dbRef.setValue(updatedUser)
            .addOnSuccessListener {
                Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show()
            }
    }
}