package com.example.pizzazone

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class OrderedhistoryFragment : Fragment(R.layout.fragment_ordered) {

    class OrderedhistoryFragment : Fragment(R.layout.fragment_ordered) {
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

            // Default fragment
            childFragmentManager.beginTransaction()
                .replace(R.id.inner_fragment_container, HomeFragment())
                .commit()

            bottomNav.setOnItemSelectedListener {
                val selectedFragment = when (it.itemId) {
                    R.id.nav_home -> HomeFragment()
                    R.id.nav_myorderhistory -> OrderedhistoryFragment()
                    R.id.nav_cart -> DisplayCartFragment()
                    R.id.nav_profile -> ProfileFragment()
                    else -> HomeFragment()
                }

                childFragmentManager.beginTransaction()
                    .replace(R.id.inner_fragment_container, selectedFragment)
                    .commit()

                true
            }
        }
        }
    }


