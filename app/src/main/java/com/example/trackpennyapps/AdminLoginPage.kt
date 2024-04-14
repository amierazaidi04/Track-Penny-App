package com.example.trackpennyapps

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminLoginPage : AppCompatActivity() {

    private lateinit var btnLog: Button
    private lateinit var email: EditText
    private lateinit var password: EditText

    //declare the firebase
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login_page)

        //declare components
        btnLog = findViewById(R.id.adminBtnLogin)
        email = findViewById(R.id.adminEmailLogin)
        password = findViewById(R.id.adminPasswordLogin)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("admin")

        btnLog.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                logIn(email, password)
            } else {
                Toast.makeText(this@AdminLoginPage, "All fields are mandatory", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    //create the function login
    //this function read data to firebase
    //p = password
    //e = email
    private fun logIn(email: String, password: String) {
        databaseReference.orderByChild("adminEmail").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (adminSnapshot in dataSnapshot.children) {
                            val admin = adminSnapshot.getValue(Admin::class.java)

                            if (admin != null && admin.adminPassword == password) {
                                Toast.makeText(
                                    this@AdminLoginPage,
                                    "Login Successful",
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this@AdminLoginPage, AdminMainActivity::class.java))
                                finish() // Finish the current activity to prevent going back to it on pressing back
                                return
                            }
                        }
                    }

                    Toast.makeText(this@AdminLoginPage, "Login Failed", Toast.LENGTH_LONG).show()

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@AdminLoginPage,
                        "Database Error: ${databaseError.message}",
                        Toast.LENGTH_LONG
                    ).show()

                }
            })
    }
}
