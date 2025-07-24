package com.example.pizzazone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager // <-- Import GridLayoutManager
import com.example.pizzazone.Adapter.AdminProductAdapter
import com.example.pizzazone.Domain.CategoryModel
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.ViewModel.MainViewModel
import com.example.pizzazone.databinding.FragmentAdminDeleteProductBinding

class AdminDeleteProductFragment : Fragment() {

    private var _binding: FragmentAdminDeleteProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adminProductAdapter: AdminProductAdapter
    private var categories: MutableList<CategoryModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDeleteProductBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecyclerView()
        setupSpinner()
        setupClickListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        adminProductAdapter = AdminProductAdapter(mutableListOf())
        binding.listView.apply {
            // Change to GridLayoutManager with 2 columns
            layoutManager = GridLayoutManager(context, 2) // <-- HERE: Set spanCount to 2
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
                        updateProductList(mutableListOf()) // Show empty list if "Select Category" is chosen
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
            // It seems 'loadPopular' is not filtering by category, so it loads all popular items.
            // If you want to load ALL items (not just popular) for "See All", you'd need a new method in MainRepository/ViewModel.
            // For now, this will show popular items.
            viewModel.loadPopular().observe(viewLifecycleOwner) { popList ->
                updateProductList(popList.toMutableList())
                binding.categorySpinner.setSelection(0) // Reset spinner to "Select Category"
            }
        }
    }

    private fun updateProductList(items: MutableList<ItemModel>) {
        // You should ideally update the existing adapter's data, not create a new adapter every time.
        // This is more efficient.
        adminProductAdapter.updateItems(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}