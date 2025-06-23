package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class Admin_homeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_adminhome, container, false)

        val btnadd = view.findViewById<Button>(R.id.btnadd1)
        val btnview = view.findViewById<Button>(R.id.btnview1)
        val btnupdate = view.findViewById<Button>(R.id.btnupdate1)

        btnadd.setOnClickListener {
            val intent = Intent(activity, AdminAddProductActivity::class.java)
            intent.putExtra("showListFragment", true)
            startActivity(intent)
        }

        btnview.setOnClickListener {
            val intent = Intent(activity, AdminViewProductActivity::class.java)
            intent.putExtra("showListFragment", true)
            startActivity(intent)
        }

        btnupdate.setOnClickListener {
            val intent = Intent(activity, AdminDeleteAndUpdateActivity::class.java)
            intent.putExtra("showListFragment", true)
            startActivity(intent)
        }

        return view
    }
}
