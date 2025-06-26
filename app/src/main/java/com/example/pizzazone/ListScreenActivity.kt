package com.example.pizzazone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer // Import Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pizzazone.Adapter.ItemsListCategoryAdapter
import com.example.pizzazone.ViewModel.MainViewModel
import com.example.pizzazone.databinding.FragmentListScreenBinding // Assuming this is your activity layout
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListScreenActivity : AppCompatActivity() { // This is an Activity, not a Fragment
    private lateinit var binding: FragmentListScreenBinding
    private val viewModel = MainViewModel()
    private var categoryId: String = "" // Renamed from 'id' for clarity
    private var categoryTitle: String = "" // Renamed from 'title' for clarity

    private lateinit var bottomNavigationView: BottomNavigationView // If this activity also has a bottom nav

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentListScreenBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set content view once

        getBundleData() // Renamed for clarity
        initList()

        // If this activity also has a bottom navigation view and manages fragments
        // You would typically have a single activity for bottom nav and replace fragments within it.
        // The current setup seems to mix concerns. If ListScreenActivity is just for displaying a list
        // based on a category, then the bottom navigation logic likely belongs in a different
        // main activity that hosts various fragments.
        // For now, I'm commenting out the fragment replacement logic if ListScreenActivity is a standalone list view.
        /*
        bottomNavigationView = findViewById(R.id.bottom_navigation) // Make sure this ID exists in your layout

        val showListFragment = intent.getBooleanExtra("showListFragment", false)

        if (savedInstanceState == null) {
            if (showListFragment) {
                // If ListScreenActivity is meant to *host* List_Screen_Fragment, then this is okay.
                // But from the context, ListScreenActivity *is* the list screen.
                // Consider if you truly need List_Screen_Fragment or if ListScreenActivity handles the list directly.
                replaceFragment(List_Screen_Fragment())
            } else {
                // replaceFragment(HomeFragment()) // This likely belongs in a main activity
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
        */
    }

    private fun getBundleData() { // Renamed for clarity
        categoryId = intent.getStringExtra("id") ?: "" // Use Elvis operator for null safety
        categoryTitle = intent.getStringExtra("title") ?: "" // Use Elvis operator for null safety

        binding.textView3.text = categoryTitle
    }

    private fun initList() {
        binding.apply {
            viewModel.loadItems(categoryId).observe(this@ListScreenActivity, Observer { items -> // Corrected Observer and context
                listView.layoutManager = LinearLayoutManager(
                    this@ListScreenActivity, // Corrected context
                    LinearLayoutManager.VERTICAL, // Corrected constant
                    false
                )
                listView.adapter = ItemsListCategoryAdapter(items) // Pass the list of items
            })
            imageView.setOnClickListener { finish() }
        }
    }

    // If ListScreenActivity is only for the list, this method might not be needed here.
    // It's typically found in a main activity managing a NavHost or fragment container.
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Make sure R.id.fragment_container exists in your layout
            .commit()
    }
}