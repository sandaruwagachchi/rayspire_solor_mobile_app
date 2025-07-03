package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pizzazone.Adapter.ItemsListCategoryAdapter
import com.example.pizzazone.List_Screen_Fragment
import com.example.pizzazone.ViewModel.MainViewModel
import com.example.pizzazone.databinding.FragmentListScreenBinding


class List_Screen_Fragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_list__screen_, container, false)


        return view
    }



    }


