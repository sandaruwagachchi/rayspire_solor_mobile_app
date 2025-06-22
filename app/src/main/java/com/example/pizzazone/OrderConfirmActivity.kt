package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OrderConfirmActivity :AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_orderconfirmscreen)


            val btnConfirm = findViewById<Button>(R.id.btnConfirm)

            btnConfirm.setOnClickListener {
                val intent = Intent(this, HomeScreenActivity::class.java)
                intent.putExtra("showHome", true)
                startActivity(intent)
                finish() // prevent coming back
            }
        }
    }

