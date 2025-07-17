package com.example.pizzazone

import CartAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView // Import TextView for subtotal
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzazone.CartManager // Import CartManager

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var textSubprice: TextView // Reference for subtotal TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize subtotal TextView
        textSubprice = view.findViewById(R.id.textSubprice)

        // Initialize adapter with an empty list initially
        cartAdapter = CartAdapter(mutableListOf()) { // Pass lambda for item changes
            updateSubtotal()
        }
        recyclerView.adapter = cartAdapter

        // Observe cart items from CartManager
        CartManager.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.updateCartItems(items) // Update adapter with new list
            updateSubtotal() // Update subtotal whenever cart items change
        }


        val buttonCheck = view.findViewById<Button>(R.id.buttonCheckout)
        buttonCheck.setOnClickListener {
            val subtotal = CartManager.cartItems.value?.sumOf { it.item.price * it.quantity } ?: 0.0
            val intent = Intent(activity, CheckoutScreenActivity::class.java)
            intent.putExtra("subtotal_amount", subtotal)
            intent.putExtra("showListFragment", true)
            startActivity(intent)
        }


        return view
    }

    private fun updateSubtotal() {
        val currentCart = CartManager.cartItems.value ?: mutableListOf()
        var subtotal = 0.0
        for (cartItem in currentCart) {
            subtotal += cartItem.item.price * cartItem.quantity
        }
        textSubprice.text = "$${String.format("%.2f", subtotal)}"
    }
}