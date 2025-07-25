package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("Customers")

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val textViewLogin = findViewById<TextView>(R.id.textViewLogin)

        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (name.isEmpty()) {
                editTextName.error = "Name is required"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                editTextEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                editTextPassword.error = "Password is required"
                return@setOnClickListener
            }
            if (password.length < 6) {
                editTextPassword.error = "Password should be at least 6 characters"
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val userId = firebaseUser?.uid

                        if (userId != null) {

                            val user = Customer(userId, name, email)
                            usersRef.child(userId).setValue(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { dbException ->

                                    Toast.makeText(this, "Failed to save user data: ${dbException.message}", Toast.LENGTH_LONG).show()
                                    dbException.printStackTrace() // debugging සඳහා
                                }
                        } else {

                            Toast.makeText(this, "User ID not found after registration.", Toast.LENGTH_LONG).show()
                        }
                    } else {

                        val errorMessage = task.exception?.message ?: "Registration failed. Please try again."
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        task.exception?.printStackTrace() // debugging සඳහා
                    }
                }
        }



        textViewLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    data class Customer(
        val userId: String = "",
        val name: String = "",
        val email: String = ""

    )
}