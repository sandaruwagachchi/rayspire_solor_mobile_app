package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class OrderConfirmScreenActivity : AppCompatActivity () {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orderconfirmscreen)

        val btnNext = findViewById<Button>(R.id.btnConfirm)

        btnNext.setOnClickListener{
            val intent = Intent(this, OrderedHistoryActivity::class.java)
            startActivity(intent)


        }}}