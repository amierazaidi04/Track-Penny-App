package com.example.trackpennyapps

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterPage : AppCompatActivity() {
    //declare  to connect with database
    private lateinit var dbRef: DatabaseReference

    //initialize all component
    private lateinit var submit: Button
    private lateinit var reset: Button
    private lateinit var name: EditText
    private lateinit var password: EditText
    private lateinit var email: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        //declare all components
        submit = findViewById(R.id.btnSubmit)
        reset = findViewById(R.id.btnReset)
        name = findViewById(R.id.eTName)
        password = findViewById(R.id.eTPassword)
        email = findViewById(R.id.eTEmail)

        submit.setOnClickListener {
            //call function saveEmployeeData
            //parameter - change the input data to string
            saveData(email.text.toString(), name.text.toString(), password.text.toString()
            )
        }

        reset.setOnClickListener {
            name.setText("")
            password.setText("")
            email.setText("")
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
            }.addOnFailureListener{
                Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show()

            }

        //declare variable i
        val i = Intent(this, MainActivity::class.java)
        intent.putExtra("userId", userId)

        startActivity(i) }
}