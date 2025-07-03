package com.example.pizzazone

import CartAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Sample cart data - replace with your real cart items or use CartManager
        val cartItems = listOf(
            CartProduct("12.8V 100AH 200AH 300AH Series Lithium Battery", "$25.00", R.drawable.battery),
            CartProduct("BR-M580-600W 210 HALF CELL SOLAR PANEL", "$80.00", R.drawable.solarpanel)
        )

        // Set adapter
        val adapter = CartAdapter(cartItems)
        recyclerView.adapter = adapter

        // Checkout button click
        val buttoncheck = view.findViewById<Button>(R.id.buttonCheckout)
        buttoncheck.setOnClickListener {
            val intent = Intent(activity, CheckoutScreenActivity::class.java)
            intent.putExtra("showListFragment", true)
            startActivity(intent)
        }

        return view
    }
}
