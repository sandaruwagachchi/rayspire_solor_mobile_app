package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class CartFragment :Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
val view = inflater.inflate(R.layout.fragment_cart, container, false)

       val buttoncheck = view.findViewById<Button>(R.id.buttoncheck)


       buttoncheck.setOnClickListener {
            val intent = Intent(activity,CheckoutScreenActivity::class.java)
            intent.putExtra("showListFragment", true)
            startActivity(intent)
        }

       return view
    }
}