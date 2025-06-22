package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class DetailsScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details_screen, container, false)

        val buttondetails = view.findViewById<Button>(R.id.buttondetails)

        buttondetails.setOnClickListener {
            DetailsScreenActivity.bottomNavViewInstance?.selectedItemId = R.id.nav_cart
        }



        return view
    }
}