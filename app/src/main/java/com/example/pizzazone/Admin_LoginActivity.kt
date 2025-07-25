package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText // Import EditText
import android.widget.Toast // Import Toast
import androidx.appcompat.app.AppCompatActivity

class Admin_LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        val buttonLogin = findViewById<Button>(R.id.buttonLoginAdmin)
        val editAdminEmail = findViewById<EditText>(R.id.editAdminEmail) // Reference to the email EditText
        val editAdminPassword = findViewById<EditText>(R.id.editAdminPassword) // Reference to the password EditText

        buttonLogin.setOnClickListener {
            // Get the email and password entered by the user
            val enteredEmail = editAdminEmail.text.toString()
            val enteredPassword = editAdminPassword.text.toString()

            // Hardcoded credentials
            val correctEmail = "admin@gmail.com"
            val correctPassword = "123456"

            // Compare the entered credentials with the hardcoded values
            if (enteredEmail == correctEmail && enteredPassword == correctPassword) {
                // Login successful
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AdminHomeScreenActivity::class.java)
                startActivity(intent)
                finish() // Close the current activity
            } else {
                // Login failed
                Toast.makeText(this, "Incorrect email or password.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}