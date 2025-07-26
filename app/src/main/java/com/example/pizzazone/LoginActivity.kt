package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient // Google Sign-in Client
    private lateinit var googleSignInImageView: ImageView // Google icon එක ImageView එකක් ලෙස

    // Google Sign-in Activity Result Launcher
    private val signInLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In සාර්ථකයි, Firebase සමග authenticate කරන්න
                val account = task.getResult(ApiException::class.java)
                Log.d("GoogleSignIn", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!) // ID token එක Firebase වෙත යවන්න
            } catch (e: ApiException) {
                // Google Sign In අසාර්ථකයි
                Log.w("GoogleSignIn", "Google sign in failed", e)
                Toast.makeText(this, "Google Sign In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // string.xml එකේ client_id එක
            .requestEmail() // පරිශීලකයාගේ ඊමේල් ලිපිනය ඉල්ලන්න
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // UI Element Initializations
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val textViewRegister = findViewById<TextView>(R.id.textViewSignUp)
        val textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)
        val textViewAdmin = findViewById<TextView>(R.id.textADMINLogin)
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        googleSignInImageView = findViewById(R.id.google) // Google icon ImageView එක initialize කරන්න

        // Set Click Listeners

        // Email/Password Login Button
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty()) {
                editTextEmail.error = "Email is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextPassword.error = "Password is required"
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        // Register TextView
        textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Forgot Password TextView
        textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordScreenActivity::class.java))
        }

        // Admin Login TextView
        textViewAdmin.setOnClickListener {
            startActivity(Intent(this, Admin_LoginActivity::class.java))
            finish()
        }

        // Back Arrow
        backArrow.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java)) // මෙය LoginActivity වෙත ආපසු යාමට නම්, එය නිවැරදිදැයි පරීක්ෂා කරන්න
            finish()
        }

        // Google Sign-in Button/Icon Click Listener
        googleSignInImageView.setOnClickListener {
            signInWithGoogle() // Google Sign-in ක්‍රියාවලිය ආරම්භ කරන්න
        }

        // Optional: Check if user is already signed in (Firebase email/password or Google)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, navigate to HomeScreenActivity
            // මෙය අවශ්‍ය නම් පමණක් තබා ගන්න, නැතිනම් remove කරන්න.
            // සමහර විට පරිශීලකයාට සෑම විටම login screen එකට ඒමට අවශ්‍ය විය හැක.
            // Toast.makeText(this, "Already signed in as ${currentUser.email}", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, HomeScreenActivity::class.java))
            // finish()
        }
    }

    // Email/Password Login Function
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeScreenActivity::class.java)) // Login සාර්ථක නම්, HomeScreenActivity වෙත යන්න
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Function to start Google Sign-in flow
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent) // ActivityResultLauncher හරහා Google Sign-in Intent එක ආරම්භ කරන්න
    }

    // Function to authenticate with Firebase using Google ID Token
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Firebase Authentication සාර්ථකයි
                    Log.d("GoogleSignIn", "signInWithCredential success")
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in with Google as ${user?.email}", Toast.LENGTH_SHORT).show()
                    // Google Sign-in සාර්ථක නම්, HomeScreenActivity වෙත යන්න
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                    finish()
                } else {
                    // Firebase Authentication අසාර්ථකයි
                    Log.w("GoogleSignIn", "signInWithCredential failed", task.exception)
                    Toast.makeText(this, "Google Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}