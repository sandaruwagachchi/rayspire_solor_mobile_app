package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class AdminDeleteAndUpdateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_admin_delete_and_update, container, false)

        val btnhome = view.findViewById<Button>(R.id.btnhome)

        btnhome.setOnClickListener {
            val intent = Intent(activity, AdminHomeScreenActivity::class.java)
            intent.putExtra("showDetailsFragment", true)
            startActivity(intent)
        }

        return view
    }
}