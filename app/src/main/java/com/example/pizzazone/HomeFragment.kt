package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pizzazone.Adapter.CategoryAdapter
import com.example.pizzazone.ViewModel.MainViewModel
import com.example.pizzazone.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Setup RecyclerView
        categoryAdapter = CategoryAdapter(mutableListOf())
        binding.recycleView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recycleView.adapter = categoryAdapter

        // Observe Category Data from Firebase
        viewModel.loadCategory().observe(viewLifecycleOwner, Observer { categoryList ->
            categoryAdapter = CategoryAdapter(categoryList)
            binding.recycleView.adapter = categoryAdapter
        })

        // Button Click
        binding.buttonlist.setOnClickListener {
            val intent = Intent(activity, ListScreenActivity::class.java)
            intent.putExtra("showListFragment", true)
            startActivity(intent)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
