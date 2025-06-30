package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register)

        val textViewLogin = findViewById<TextView>(R.id.textViewLogin)

        textViewLogin.setOnClickListener {
                 val intent = Intent(this, LoginActivity::class.java)
                 startActivity(intent)
                  finish()

             }


    }
}