// උදාහරණයක් ලෙස ඔබේ MainActivity.kt (හෝ මේ Fragment එක host කරන Activity එක)

package com.example.pizzazone

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate // මේක import කරගන්න

class MainActivity : AppCompatActivity() { // ඔබේ ප්‍රධාන Activity එකේ නම මෙතන දාන්න

    override fun onCreate(savedInstanceState: Bundle?) {
        // --- Theme Load Logic ---
        // යෙදුම ආරම්භ වීමට පෙර save කරගත් තීම් එක load කරන්න
        loadSavedTheme()
        // --- End Theme Load Logic ---

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // ඔබේ ප්‍රධාන layout එක මෙතන දාන්න

        // ... ඔබේ අනෙකුත් Activity කෝඩ් ...
    }

    // --- Theme Load Function ---
    private fun loadSavedTheme() {
        val sharedPref = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val savedTheme = sharedPref.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedTheme)
    }
}