package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class CheckoutScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_checkout_screen, container, false)

        val buttonconfirm = view.findViewById<Button>(R.id.buttonconfirm)

        val buttongo = view.findViewById<Button>(R.id.buttongo)

        buttonconfirm.setOnClickListener {
            val intent = Intent(activity, OrderConfirmActivity::class.java)
            startActivity(intent)
        }

        buttongo.setOnClickListener {
            val intent = Intent(activity,OderInfoActivity::class.java)
            startActivity(intent)
        }


        return view
    }
}
