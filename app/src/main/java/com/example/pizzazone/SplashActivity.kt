package com.example.pizzazone

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    // Total duration for the splash screen before transitioning to the next activity
    private val TOTAL_SPLASH_DELAY: Long = 3000 // 3 seconds

    // Duration for the fade-in and upward movement animation of each text line
    private val ANIMATION_DURATION: Long = 800 // 800 milliseconds

    // Delay between the completion of the first line's animation and the start of the second line's animation
    private val DELAY_BETWEEN_LINES: Long = 200 // 200 milliseconds

    // TextViews for "Rayspire" and "Solar"
    private lateinit var rayspireTextView: TextView
    private lateinit var solarTextView: TextView

    // Handler for managing delayed tasks and animations
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view for this activity
        setContentView(R.layout.activity_splash)

        // Set fullscreen flags to hide status bar for an immersive splash experience
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            // For older Android versions
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Initialize TextViews by finding them in the layout
        rayspireTextView = findViewById(R.id.rayspire_text_view)
        solarTextView = findViewById(R.id.solar_text_view)

        // Set initial state for animation:
        // - alpha to 0f makes them completely transparent (invisible)
        // - translationY to 50f positions them slightly below their final resting place
        rayspireTextView.alpha = 0f
        rayspireTextView.translationY = 50f
        solarTextView.alpha = 0f
        solarTextView.translationY = 50f

        // Initialize the Handler with the main Looper to run tasks on the UI thread
        handler = Handler(Looper.getMainLooper())

        // Start the animation for the "Rayspire" TextView
        // An AnimatorListenerAdapter is used to trigger the next animation after "Rayspire" finishes
        animateTextView(rayspireTextView, object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                // Once "Rayspire" animation completes, post a delayed task to start "Solar" animation
                handler.postDelayed({
                    // Animate "Solar" TextView. No specific listener needed for "Solar" as it's the last animation.
                    animateTextView(solarTextView, null)
                }, DELAY_BETWEEN_LINES) // Delay before "Solar" animation starts
            }
        })

        // Schedule the transition to the next activity (Onboarding1Activity)
        // This will happen after the TOTAL_SPLASH_DELAY, regardless of when animations finish.
        handler.postDelayed({
            val intent = Intent(this@SplashActivity, Onboarding1Activity::class.java)
            startActivity(intent) // Start the next activity
            finish() // Finish the current SplashActivity so it's not on the back stack
        }, TOTAL_SPLASH_DELAY)
    }

    /**
     * Animates a given TextView with a fade-in (alpha) and an upward translation (translationY) effect.
     * @param textView The TextView to apply the animation to.
     * @param listener An optional AnimatorListenerAdapter to attach to the alpha animation.
     * This listener's onAnimationEnd will be called when the fade-in animation completes.
     */
    private fun animateTextView(textView: TextView, listener: AnimatorListenerAdapter?) {
        // Create an ObjectAnimator for the alpha property (fade-in from 0% to 100% opacity)
        val alphaAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
        alphaAnimator.duration = ANIMATION_DURATION // Set the duration for the fade-in

        // Create an ObjectAnimator for the translationY property (move from 50dp below to its original position)
        val translationAnimator = ObjectAnimator.ofFloat(textView, "translationY", 50f, 0f)
        translationAnimator.duration = ANIMATION_DURATION // Set the duration for the upward movement

        // Start both animations simultaneously
        alphaAnimator.start()
        translationAnimator.start()

        // If a listener is provided, attach it to the alpha animator.
        // This allows for sequential animations or other actions upon completion.
        listener?.let {
            alphaAnimator.addListener(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove all pending callbacks and messages from the handler to prevent memory leaks
        // This is crucial if the activity is destroyed before all delayed tasks are executed.
        handler.removeCallbacksAndMessages(null)
    }
}
