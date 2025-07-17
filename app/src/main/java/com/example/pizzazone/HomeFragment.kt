package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager // Import GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pizzazone.Adapter.CategoryAdapter
import com.example.pizzazone.Adapter.PopularAdapter
import com.example.pizzazone.ViewModel.MainViewModel
import com.example.pizzazone.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularAdapter: PopularAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Setup category RecyclerView (horizontal)
        categoryAdapter = CategoryAdapter(mutableListOf())
        binding.recycleView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        viewModel.loadCategory().observe(viewLifecycleOwner) { list ->
            categoryAdapter = CategoryAdapter(list)
            binding.recycleView.adapter = categoryAdapter
        }

        popularAdapter = PopularAdapter(mutableListOf())
        binding.recycleView2.apply {
            // *** MODIFIED LINE HERE: Use GridLayoutManager for two items per row ***
            layoutManager = GridLayoutManager(context, 2) // Set span count to 2
            adapter = popularAdapter
        }
        viewModel.loadPopular().observe(viewLifecycleOwner) { popList ->
            popularAdapter = PopularAdapter(popList.toMutableList())
            binding.recycleView2.adapter = popularAdapter
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}