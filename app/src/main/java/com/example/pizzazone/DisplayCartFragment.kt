package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class DisplayCartFragment :Fragment(R.layout.fragment_displaycart) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnNext = view.findViewById<Button>(R.id.btncart)
        btnNext.setOnClickListener {
            val intent = Intent(requireContext(), CheckOutScreenActivity::class.java)
            startActivity(intent)
        }
    }


}