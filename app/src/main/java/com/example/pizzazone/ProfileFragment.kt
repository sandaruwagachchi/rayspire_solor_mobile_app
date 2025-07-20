package com.example.pizzazone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate // මේක import කරගන්න
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    // UI elements
    private lateinit var greetingText: TextView
    private lateinit var textViewProfileName: TextView
    private lateinit var textViewProfileEmail: TextView
    private lateinit var logoutButton: Button
    private lateinit var backArrow: ImageView
    private lateinit var leftToRightImage: ImageView // leftToRight ImageView එකට variable එකක්

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize UI elements from the inflated view
        greetingText = view.findViewById(R.id.greetingText)
        textViewProfileName = view.findViewById(R.id.textViewProfileName)
        textViewProfileEmail = view.findViewById(R.id.textViewProfileEmail)
        logoutButton = view.findViewById(R.id.buttonLogout)
        backArrow = view.findViewById(R.id.backArrow)
        leftToRightImage = view.findViewById(R.id.leftToRight) // leftToRight ImageView එක හඳුනාගන්න

        // Set up click listeners
        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        // --- Dark/Light Mode Toggle Logic ---
        leftToRightImage.setOnClickListener {
            toggleAppTheme()
        }
        // --- End Dark/Light Mode Toggle Logic ---

        // Load user profile data
        loadUserProfile()

        return view
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = database.getReference("Customers").child(userId)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val customer = snapshot.getValue(Customer::class.java)
                        customer?.let {
                            greetingText.text = "Hi, ${it.name}!"
                            textViewProfileName.text = "${it.name}"
                            textViewProfileEmail.text = "${it.email}"
                        }
                    } else {
                        Toast.makeText(requireContext(), "User data not found in database.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load profile: ${error.message}", Toast.LENGTH_SHORT).show()
                    error.toException().printStackTrace()
                }
            })
        } else {
            Toast.makeText(requireContext(), "No user logged in. Please login.", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    // --- Dark/Light Mode Toggle Function ---
    private fun toggleAppTheme() {
        // වත්මන් තීම් එක ලබා ගන්නවා
        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK

        // තීම් එක මාරු කරනවා
        when (currentNightMode) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> {
                // දැනට Dark Mode නම්, Light Mode එකට මාරු කරන්න
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(requireContext(), "Switched to Light Mode", Toast.LENGTH_SHORT).show()
            }
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
                // දැනට Light Mode නම්, Dark Mode එකට මාරු කරන්න
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(requireContext(), "Switched to Dark Mode", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Default (system default) නම්, Dark Mode එකට මාරු කරන්න
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(requireContext(), "Switched to Dark Mode", Toast.LENGTH_SHORT).show()
            }
        }

        // තීම් එක මාරු වූ පසු, Activity එක නැවත create කරන්න.
        // මෙය සියලු Views වලට අලුත් තීම් එක යෙදීමට උපකාරී වේ.
        requireActivity().recreate()
    }

    // --- Theme Preference Save/Load Functions ---
    private fun saveThemePreference(mode: Int) {
        val sharedPref = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("theme_mode", mode)
            apply()
        }
    }

    private fun loadThemePreference(): Int {
        val sharedPref = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        // Default value එක MODE_NIGHT_FOLLOW_SYSTEM ලෙස සලකමු
        return sharedPref.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    // Customer Data Class (same as before)
    data class Customer(
        val userId: String = "",
        val name: String = "",
        val email: String = ""
    )
}