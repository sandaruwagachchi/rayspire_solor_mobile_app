package com.example.pizzazone // Make sure this matches your actual package name

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth // Import Firebase Authentication

class ForgotPasswordScreenActivity : AppCompatActivity() {

    // Declare Firebase Auth instance
    private lateinit var auth: FirebaseAuth

    // Declare UI elements
    private lateinit var backArrow: ImageView
    private lateinit var editTextEmail: EditText
    private lateinit var buttonCancel: Button
    private lateinit var buttonReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpasswordscreen)

        // Initialize Firebase Auth instance
        auth = FirebaseAuth.getInstance()

        // Initialize UI elements from your layout
        backArrow = findViewById(R.id.backArrow)
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonCancel = findViewById(R.id.buttonCancel)
        buttonReset = findViewById(R.id.buttonReset)

        // Set up click listener for the back arrow
        backArrow.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finish the current activity
        }

        // Set up click listener for the Cancel button
        buttonCancel.setOnClickListener {
            // You can add logic here if you want to do something specific
            // before going back, e.g., clear fields or show a message.
            Toast.makeText(this, "Password reset cancelled.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Go back to LoginActivity
        }

        // Set up click listener for the Reset button
        buttonReset.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val email = editTextEmail.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            editTextEmail.error = "Email is required!"




            return
        }

        // Optional: Basic email format validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Please enter a valid email address!"
            editTextEmail.requestFocus()
            return
        }

        // Send password reset email using Firebase Auth
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_LONG).show()
                    // Optionally, navigate back to the login screen after success
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Handle failures
                    val errorMessage = task.exception?.message ?: "Failed to send reset email. Please try again."
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }
}