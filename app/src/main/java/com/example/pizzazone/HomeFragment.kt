package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout and assign to variable
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Access the button inside the view
        val buttonlist = view.findViewById<Button>(R.id.buttonlist)

        // Set click listener
        buttonlist.setOnClickListener {
            val intent = Intent(activity, ListScreenActivity::class.java)
            intent.putExtra("showListFragment", true)
            startActivity(intent)
        }

        // Return the view
        return view
    }
}



















