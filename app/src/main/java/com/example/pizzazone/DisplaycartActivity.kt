package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DisplaycartActivity :AppCompatActivity () {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_displaycart)

        val btnNext = findViewById<Button>(R.id.btncart)

        btnNext.setOnClickListener {
            val intent = Intent(this, CheckOutScreenActivity::class.java)
            startActivity(intent)


        }
    }
}
