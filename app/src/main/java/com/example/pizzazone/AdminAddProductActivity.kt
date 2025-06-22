package com.example.pizzazone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminAddProductActivity:AppCompatActivity() {


    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_product)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val showListFragment = intent.getBooleanExtra("showListFragment", false)

        if (savedInstanceState == null) {
            if (showListFragment) {
                replaceFragment(AdminAddProductFragment())
            } else {
                replaceFragment(Admin_homeFragment())
            }
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(Admin_homeFragment())
                    true
                }
                R.id.nav_order -> {
                    replaceFragment(RecivedOrderFragment())
                    true
                }
                R.id.nav_income -> {
                    replaceFragment(IncomeFragment())
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