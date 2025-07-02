package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Admin_LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        val buttonLogin = findViewById<Button>(R.id.buttonLoginAdmin)



        buttonLogin.setOnClickListener {
            val intent = Intent(this, AdminHomeScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


}