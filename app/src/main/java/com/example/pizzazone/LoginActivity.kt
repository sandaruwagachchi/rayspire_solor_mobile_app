package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login)

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        val textViewSignUp = findViewById<TextView>(R.id.textViewSignUp)
        val forgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)

        buttonLogin.setOnClickListener{
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        backArrow.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()

        }

        textViewSignUp.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

         forgotPassword.setOnClickListener{
             val intent = Intent(this, forgotPassword::class.java)
             startActivity(intent)
             finish()

         }

}}