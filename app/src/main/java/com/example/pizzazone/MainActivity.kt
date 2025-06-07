package com.example.pizzazone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), HomeFragment.OnHomeFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Default fragment එක HomeFragment එක load කරන්න
        replaceFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_myorderhistory -> replaceFragment(OrderedhistoryFragment())
                R.id.nav_cart -> replaceFragment(DisplayCartFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commit()
    }

    // HomeFragment එකෙන් button click call එක receive කරන්න
    override fun onHomeHistoryButtonClicked() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        replaceFragment(OrderedhistoryFragment())
        bottomNav.selectedItemId = R.id.nav_myorderhistory
    }
}
