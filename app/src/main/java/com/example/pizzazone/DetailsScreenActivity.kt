package com.example.pizzazone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.pizzazone.Domain.ItemModel

class DetailsScreenActivity : AppCompatActivity() {


    companion object {
        var bottomNavViewInstance: BottomNavigationView? = null
        const val EXTRA_ITEM_OBJECT = "item_object"
    }

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_details_screen)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavViewInstance = bottomNavigationView


        val itemModel = intent.getSerializableExtra(EXTRA_ITEM_OBJECT) as? ItemModel

        if (savedInstanceState == null) {

            if (itemModel != null) {
                val detailsFragment = DetailsScreenFragment()
                val bundle = Bundle()
                bundle.putSerializable(EXTRA_ITEM_OBJECT, itemModel)
                detailsFragment.arguments = bundle
                replaceFragment(detailsFragment)
            } else {

                replaceFragment(HomeFragment())
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