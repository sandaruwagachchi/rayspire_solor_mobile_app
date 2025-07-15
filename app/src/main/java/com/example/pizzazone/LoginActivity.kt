package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val textViewRegister = findViewById<TextView>(R.id.textViewSignUp)
        val textviewADMIN = findViewById<TextView>(R.id.textADMINLogin)
        val textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)
        val backArrow = findViewById<ImageView>(R.id.backArrow)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim().lowercase()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty()) {
                editTextEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                editTextPassword.error = "Password is required"
                return@setOnClickListener
            }

            validateLogin(email, password)
        }

        backArrow.setOnClickListener {
            startActivity(Intent(this, HomeScreenActivity::class.java))
            finish()
        }

        textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordScreenActivity::class.java))
        }

        textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        textviewADMIN.setOnClickListener {
            startActivity(Intent(this, Admin_LoginActivity::class.java))
            finish()
        }
    }

    private fun validateLogin(email: String, password: String) {
        val customerRef = FirebaseDatabase.getInstance().getReference("Customers")

        customerRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (customerSnapshot in snapshot.children) {
                            val dbPassword = customerSnapshot.child("password").getValue(String::class.java)
                            if (dbPassword == password) {
                                val intent = Intent(this@LoginActivity, HomeScreenActivity::class.java)
                                startActivity(intent)
                                finish()
                                return
                            }
                        }
                        Toast.makeText(this@LoginActivity, "Incorrect password", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "Email not registered", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
