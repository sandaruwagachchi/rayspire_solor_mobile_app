package com.example.pizzazone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailsScreenActivity :AppCompatActivity() {

    companion object {
        var bottomNavViewInstance: BottomNavigationView? = null
    }

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_screen)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavViewInstance = bottomNavigationView

        val showDetailsFragment = intent.getBooleanExtra("showDetailsFragment", false)
        val showListFragment = intent.getBooleanExtra("showListFragment", false)

        if (savedInstanceState == null) {
            when {
                showDetailsFragment -> replaceFragment(DetailsScreenFragment())
                showListFragment -> replaceFragment(List_Screen_Fragment())
                else -> replaceFragment(HomeFragment())
            }
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
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}