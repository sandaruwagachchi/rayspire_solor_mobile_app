package com.example.pizzazone

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val rayspireTextView: TextView = findViewById(R.id.rayspire_text_view)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        rayspireTextView.startAnimation(fadeInAnimation)

        val rayspire_logo_image_view: ImageView= findViewById(R.id.rayspire_logo_image_view)
        val fadeInAnimation2 = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        rayspire_logo_image_view.startAnimation(fadeInAnimation2)


        // Make the activity fullscreen in a backward-compatible way
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Delay the transition to the next activity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashActivity, Onboarding1Activity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}

