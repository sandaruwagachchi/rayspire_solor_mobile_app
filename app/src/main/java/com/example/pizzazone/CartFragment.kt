package com.example.pizzazone

import CartAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView // Import ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var textSubprice: TextView
    private lateinit var backArrow: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        textSubprice = view.findViewById(R.id.textSubprice)

        cartAdapter = CartAdapter(mutableListOf()) {
            updateSubtotal()
        }
        recyclerView.adapter = cartAdapter

        CartManager.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.updateCartItems(items)
            updateSubtotal()
        }

        val buttonCheck = view.findViewById<Button>(R.id.buttonCheckout)
        buttonCheck.setOnClickListener {
            val subtotal = CartManager.cartItems.value?.sumOf { it.item.price * it.quantity } ?: 0.0
            val intent = Intent(activity, CheckoutScreenActivity::class.java)
            intent.putExtra("subtotal_amount", subtotal)
            intent.putExtra("showListFragment", true)
            startActivity(intent)
        }


        backArrow = view.findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(requireContext(), HomeScreenActivity::class.java)
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