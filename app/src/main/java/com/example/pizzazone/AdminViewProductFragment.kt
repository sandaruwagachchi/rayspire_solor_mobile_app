package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pizzazone.Adapter.AdminProductAdapter
import com.example.pizzazone.Domain.CategoryModel
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.ViewModel.MainViewModel
import com.example.pizzazone.databinding.FragmentAdminViewProductBinding

class AdminViewProductFragment : Fragment() {

    private var _binding: FragmentAdminViewProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adminProductAdapter: AdminProductAdapter
    private var categories: MutableList<CategoryModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminViewProductBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecyclerView()
        setupSpinner()
        setupClickListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        adminProductAdapter = AdminProductAdapter(mutableListOf())
        binding.listView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adminProductAdapter
        }
    }

    private fun setupSpinner() {
        viewModel.loadCategory().observe(viewLifecycleOwner) { categoryList ->
            categories = categoryList

            val categoryNames = categoryList.map { it.title ?: "Unknown" }.toMutableList()
            categoryNames.add(0, "Select Category")

            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = spinnerAdapter

            binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position > 0) {
                        val selectedCategory = categories[position - 1]
                        selectedCategory.id?.let { categoryId ->
                            viewModel.loadItems(categoryId.toString()).observe(viewLifecycleOwner) { items ->
                                updateProductList(items)
                            }
                        }
                    } else {
                        updateProductList(mutableListOf())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.backArrow.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.seeAll.setOnClickListener {
            viewModel.loadPopular().observe(viewLifecycleOwner) { popList ->
                updateProductList(popList.toMutableList())
                binding.categorySpinner.setSelection(0)
            }
        }


    }

    private fun updateProductList(items: MutableList<ItemModel>) {
        adminProductAdapter = AdminProductAdapter(items)
        binding.listView.adapter = adminProductAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}