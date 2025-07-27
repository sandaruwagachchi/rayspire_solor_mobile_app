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

// Ensure the Customer data class is defined here or imported from a common file.
// If it's defined at the top of ProfileFragment.kt, this file will need to be in the same package
// or you'll need to move Customer to a separate, common file/package.
// Example: data class Customer(...) is visible if both are in com.example.pizzazone.

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

            // Register user with Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val userId = firebaseUser?.uid

                        if (userId != null) {
                            // Save user data to Firebase Realtime Database
                            // Crucially, the Customer data class should *not* contain the password for database storage.
                            val user = Customer(userId, name, email, null) // profileImageUrl is initially null
                            usersRef.child(userId).setValue(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                                    // Redirect to Login Activity
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish() // Close current activity
                                }
                                .addOnFailureListener { dbException ->
                                    // If saving data fails, clean up auth user to prevent orphaned accounts
                                    firebaseUser.delete()
                                    Toast.makeText(this, "Failed to save user data: ${dbException.message}. Account not created.", Toast.LENGTH_LONG).show()
                                    dbException.printStackTrace()
                                }
                        } else {
                            Toast.makeText(this, "User ID not found after registration.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Registration failed (e.g., weak password, email already in use)
                        val errorMessage = task.exception?.message ?: "Registration failed. Please try again."
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        task.exception?.printStackTrace()
                    }
                }
        }

        // Navigate to Login Activity when "Login" link is clicked
        textViewLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}