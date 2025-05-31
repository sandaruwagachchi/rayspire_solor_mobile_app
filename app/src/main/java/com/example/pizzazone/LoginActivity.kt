package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LoginActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val btnNext = findViewById<Button>(R.id.loginbtn)

        btnNext.setOnClickListener{
            val intent = Intent(this, AddpizzaActivity::class.java)
            startActivity(intent)
        }
    }

}