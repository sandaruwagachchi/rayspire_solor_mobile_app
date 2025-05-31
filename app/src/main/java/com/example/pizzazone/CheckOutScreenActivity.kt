package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class CheckOutScreenActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkoutscreen)

        val btnNext = findViewById<Button>(R.id.btnenter)

        btnNext.setOnClickListener{
            val intent = Intent(this, OrderConfirmScreenActivity::class.java)
            startActivity(intent)

        }}}