package com.example.pizzazone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.badge.BadgeDrawable // Import BadgeDrawable

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_myorder -> {
                    replaceFragment(MyOrderFragment())
                    true
                }
                R.id.nav_cart -> {
                    replaceFragment(CartFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        // *** OBSERVE CART ITEM COUNT FOR BADGE ***
        CartManager.cartItemCount.observe(this) { count ->
            val cartBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart)
            if (count > 0) {
                cartBadge.isVisible = true
                cartBadge.number = count
                cartBadge.maxCharacterCount = 3 // To prevent very large numbers from overflowing
            } else {
                cartBadge.isVisible = false
                cartBadge.clearNumber()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}