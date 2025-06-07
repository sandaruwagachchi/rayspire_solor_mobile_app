package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity :AppCompatActivity() {



    private lateinit var Name: EditText
    private lateinit var Email: EditText
    private lateinit var Phone: EditText
    private lateinit var Address : EditText
    private lateinit var Password: EditText
    private lateinit var ConfirmPassword: EditText
    private lateinit var Submit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)


        Name = findViewById(R.id.Name)
        Email = findViewById(R.id.Email)
        Phone =findViewById(R.id.Phone)
        Address =findViewById(R.id.Address)
        Password = findViewById(R.id.Password)
        ConfirmPassword = findViewById(R.id.ConfirmPassword)
        Submit = findViewById(R.id.btnRegister)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Submit.setOnClickListener() {
            if (Registration()) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Registration successful nam, current activity eka close karanna puluwan
            }
        }
    }

    private fun Registration() : Boolean  {
        val inputName = Name.text.toString().trim()
        val inputEmail = Email.text.toString().trim()
        val inputPhone = Phone.text.toString().trim()
        val inputAddress = Address.text.toString()
        val inputPassword = Password.text.toString()
        val inputConfirmPassword = ConfirmPassword.text.toString()


        val nameContainsDigit = inputName.any { it.isDigit() }

        if (nameContainsDigit) {
            Toast.makeText(this, "Name should not contain number!", Toast.LENGTH_SHORT).show()
            Name.error = "Name should not contain numbers!"
            Name.setText("")
            return  false
        }

        if (inputName.isEmpty() || inputEmail.isEmpty() || inputPassword.isEmpty() || inputConfirmPassword.isEmpty() || inputPhone.isEmpty() ||inputAddress.isEmpty()) {
            Toast.makeText(this, "User registered is not Successfully", Toast.LENGTH_SHORT).show()
            if(inputName.isEmpty()) Name.error = "Name is required!"
            if(inputEmail.isEmpty()) Email.error = "Email is required!"
            if(inputPhone.isEmpty()) Phone.error = "Phone number is reddu!"
            if(inputPassword.isEmpty()) Password.error ="Password is required!"
            return  false
        }
        if (inputPassword != inputConfirmPassword) {
            Toast.makeText(this, "Confirm Password is not Correct!", Toast.LENGTH_SHORT).show()
            ConfirmPassword.error = "Re_Enter Password!"
            ConfirmPassword.setText("")
            return  false
        }
        Toast.makeText(this,"User registered is Successfully!", Toast.LENGTH_SHORT).show()
        return true
    }
}