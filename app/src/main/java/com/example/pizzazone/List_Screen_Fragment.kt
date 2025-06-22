package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.pizzazone.List_Screen_Fragment


class List_Screen_Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_list__screen_, container, false)

        val buttondetai = view.findViewById<Button>(R.id.buttondetai)

        buttondetai.setOnClickListener {
            val intent = Intent(activity, DetailsScreenActivity::class.java)
            intent.putExtra("showDetailsFragment", true)
            startActivity(intent)
        }



        return view
    }

    }


