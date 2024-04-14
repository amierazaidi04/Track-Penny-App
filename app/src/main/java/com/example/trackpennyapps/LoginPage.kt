package com.example.trackpennyapps

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginPage : AppCompatActivity() {

    private lateinit var btnLog : Button
    private lateinit var btnReg : Button
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var btnAdmin : ImageButton

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        btnLog = findViewById(R.id.btnLogin)
        btnReg = findViewById(R.id.btnRegister)
        email = findViewById(R.id.emailLogin)
        password = findViewById(R.id.passwordLogin)
        btnAdmin = findViewById(R.id.adminButton)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("user")

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("loggedIn", false)) {
            val userId = sharedPreferences.getString("userId", "")
            if (userId != null && userId.isNotEmpty()) {
                redirectToMainActivity(userId)
                finish()
            }
        }
        btnReg.setOnClickListener {
            val i = Intent(this, RegisterPage::class.java)
            startActivity(i)
        }

        btnLog.setOnClickListener {
            val userEmail = email.text.toString()
            val userPassword = password.text.toString()

            if(userEmail.isNotEmpty() && userPassword.isNotEmpty())
            {
                logIn(userEmail, userPassword)
            } else {
                Toast.makeText(this@LoginPage, "All fields are mandatory", Toast.LENGTH_LONG).show()
            }
        }

        btnAdmin.setOnClickListener {
            val i = Intent(this, AdminLoginPage::class.java)
            startActivity(i)
        }
    }

    private fun logIn(email: String, password: String) {
        databaseReference.orderByChild("userEmail").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val model = userSnapshot.getValue(Model::class.java)

                        if (model != null && model.userPassword == password) {
                            Toast.makeText(this@LoginPage, "Login Successful", Toast.LENGTH_LONG).show()

                            // Save login status and user ID in SharedPreferences
                            val userId = model.userId
                            if (userId != null) {
                                sharedPreferences.edit().putBoolean("loggedIn", true).apply()
                                sharedPreferences.edit().putString("userId", userId).apply()

                                // Redirect the user to MainActivity
                                redirectToMainActivity(userId)
                                return
                            } else {
                                Toast.makeText(this@LoginPage, "User ID is null", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                Toast.makeText(this@LoginPage, "Login Failed", Toast.LENGTH_LONG).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@LoginPage, "Database Error: ${databaseError.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun redirectToMainActivity(userId: String) {
        val intent = Intent(this@LoginPage, MainActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

}
