package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class OderInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_oderer_infromation, container, false)

        val buttonsave = view.findViewById<Button>(R.id.buttonsave)

        buttonsave.setOnClickListener {
            val intent = Intent(requireContext(), CheckoutScreenActivity::class.java)
            intent.putExtra("showHome", true)
            startActivity(intent)
        }

        return view
    }
}
