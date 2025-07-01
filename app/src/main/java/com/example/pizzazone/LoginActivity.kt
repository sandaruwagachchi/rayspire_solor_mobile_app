package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        val textsingup = findViewById<TextView>(R.id.textViewSignUp)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)


        backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonLogin.setOnClickListener {
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
            finish()

        }
        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        textsingup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }}