package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView

class CheckoutScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_checkout_screen, container, false)

        val buttonconfirm = view.findViewById<Button>(R.id.buttonconfirm)

        val backArrow = view.findViewById<ImageView>(R.id.shipping_back_arrow)

        val edit_shop = view.findViewById<ImageView>(R.id.edit_shipping_icon)


        buttonconfirm.setOnClickListener {
            val intent = Intent(activity, OrderConfirmActivity::class.java)
            startActivity(intent)
        }

        edit_shop.setOnClickListener {
            val intent = Intent(activity,OderInfoActivity::class.java)
            startActivity(intent)
        }

        backArrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .addToBackStack(null)
                .commit()
        }




        return view
    }
}
