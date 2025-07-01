package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pizzazone.R.layout.fragment_profile

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        // Inflate the layout first and store it in a variable
        val view = inflater.inflate(fragment_profile, container, false)


        val backArrow = view.findViewById<ImageView>(R.id.backArrow)
        val logoutButton = view.findViewById<Button>(R.id.buttonLogout)



        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        logoutButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}
