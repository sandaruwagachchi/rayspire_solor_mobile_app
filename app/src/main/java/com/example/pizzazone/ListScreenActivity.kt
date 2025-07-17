package com.example.pizzazone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pizzazone.Adapter.ItemsListCategoryAdapter
import com.example.pizzazone.ViewModel.MainViewModel
import com.example.pizzazone.databinding.FragmentListScreenBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListScreenActivity : AppCompatActivity() {
    private lateinit var binding: FragmentListScreenBinding
    private val viewModel = MainViewModel()
    private var categoryId: String = ""
    private var categoryTitle: String = ""

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentListScreenBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set content view once

        getBundleData()
        initList()


        /*
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val showListFragment = intent.getBooleanExtra("showListFragment", false)

        if (savedInstanceState == null) {
            if (showListFragment) {

                replaceFragment(List_Screen_Fragment())
            } else {
                // replaceFragment(HomeFragment())
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

    private fun getBundleData() {
        categoryId = intent.getStringExtra("id") ?: ""
        categoryTitle = intent.getStringExtra("title") ?: ""

        binding.textView3.text = categoryTitle
    }

    private fun initList() {
        binding.apply {
            viewModel.loadItems(categoryId).observe(this@ListScreenActivity, Observer { items ->
                listView.layoutManager = LinearLayoutManager(
                    this@ListScreenActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                listView.adapter = ItemsListCategoryAdapter(items)
            })
            backArrow.setOnClickListener { finish() }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}