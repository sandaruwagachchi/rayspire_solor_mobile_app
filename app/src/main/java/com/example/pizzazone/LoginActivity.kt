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

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val textViewRegister = findViewById<TextView>(R.id.textViewSignUp)
        val buttonAdminLogin = findViewById<Button>(R.id.buttonAdminLogin)
        val textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)

        buttonLogin.setOnClickListener {
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
        }

        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordScreenActivity::class.java)
            startActivity(intent)
        }

        buttonAdminLogin.setOnClickListener {
            val intent = Intent(this, AdminHomeScreenActivity::class.java)
            startActivity(intent)
        }

        textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
