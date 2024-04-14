package com.example.trackpennyapps

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddUserActivity : AppCompatActivity() {
    //declare  to connect with database
    private lateinit var dbRef: DatabaseReference

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var addUserBtn: Button
    private lateinit var closeBtn: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        addUserBtn = findViewById(R.id.addUserBtn)
        closeBtn = findViewById(R.id.closeBtn)

        addUserBtn.setOnClickListener {
            //call function saveEmployeeData
            //parameter - change the input data to string
            saveData(
                emailInput.text.toString(), nameInput.text.toString(), passwordInput.text.toString()
            )
        }

        closeBtn.setOnClickListener {
            finish()
        }

    }

    //create the function saveData
    // this function send data to firebase
    // n - name
    //p - password
    //e - email

    private fun saveData(e: String, n: String, p: String) {
        //getInstance = get object
        //link database named user
        dbRef = FirebaseDatabase.getInstance().getReference("user")

        //produce auto generate customer id
        //!! refer to must had record id or id cannot null
        val userId = dbRef.push().key!!

        //customer is object
        //push the data to database
        //customerId will autogenerate
        //data will output by user
        //input name, password, phone, email
        val em = Model(e, userId, n, p)

        //setting to push data inside table
        dbRef.child(userId).setValue(em)

            //success record
            .addOnCompleteListener {
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                //fail
            }.addOnFailureListener {
                Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show()

            }
    }
}