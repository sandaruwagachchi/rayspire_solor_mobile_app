package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonlog = findViewById<Button>(R.id.buttonLogin)
 
        val textViewreg = findViewById<TextView>(R.id.textViewreg) // "Please register"
        val buttonadmin = findViewById<Button>(R.id.buttonadmin)
        val textViewForgot = findViewById<TextView>(R.id.textViewForgot)
        val textViewreg = findViewById<TextView>(R.id.textViewSignUp)
        val buttonadmin = findViewById<Button>(R.id.buttonAdminLogin)
        val textViewForgot =findViewById<TextView>(R.id.textViewForgotPasswordi main

        val textViewSignUp = findViewById<TextView>(R.id.textViewSignUp) // "Sign Up" bold text
        val textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword) // inside card

        buttonlog.setOnClickListener {
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
        }

        textViewForgot.setOnClickListener {
            val intent = Intent(this, ForgotPasswordScreenActivity::class.java)
            startActivity(intent)
        }

        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordScreenActivity::class.java)
            startActivity(intent)
        }

        buttonadmin.setOnClickListener {
            val intent = Intent(this, AdminHomeScreenActivity::class.java)
            startActivity(intent)
        }

        textViewreg.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        textViewSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
