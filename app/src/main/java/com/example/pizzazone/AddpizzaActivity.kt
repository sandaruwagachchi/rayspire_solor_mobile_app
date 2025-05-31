package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class AddpizzaActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_addpizza)

        val btnNext = findViewById<Button>(R.id.btnadd)

        btnNext.setOnClickListener{
            val intent = Intent(this,DisplaycartActivity::class.java)
            startActivity(intent)
        }



    }

}