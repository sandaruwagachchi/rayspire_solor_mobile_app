package com.example.pizzazone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pizzazone.Adapter.AdminViewProductAdapter
import com.example.pizzazone.Domain.CategoryModel
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.ViewModel.MainViewModel
import com.example.pizzazone.databinding.FragmentAdminViewAndUpdateBinding

class AdminViewAndUpdateFragment : Fragment() {

    private var _binding: FragmentAdminViewAndUpdateBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adminViewProductAdapter: AdminViewProductAdapter
    private var categories: MutableList<CategoryModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminViewAndUpdateBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecyclerView()
        setupSpinner()
        setupClickListeners()

        // *** IMPORTANT ADDITION: Load default items immediately ***
        // This will display popular items when the fragment first loads.
        loadDefaultItems() // Call this new function

        return binding.root
    }

    private fun setupRecyclerView() {
        adminViewProductAdapter = AdminViewProductAdapter(mutableListOf()) { itemClicked ->
            navigateToProductDetails(itemClicked)
        }
        binding.listView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = adminViewProductAdapter
        }
    }

    private fun setupSpinner() {
        viewModel.loadCategory().observe(viewLifecycleOwner) { categoryList ->
            categories = categoryList

            val categoryNames = categoryList.map { it.title ?: "Unknown" }.toMutableList()
            categoryNames.add(0, "Select Category") // Add a default option

            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = spinnerAdapter

            // No need to set selection here initially, loadDefaultItems handles initial data.
            // If you want "Select Category" to be visually selected, it will be by default.

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
                        // If "Select Category" is chosen, load popular items again
                        loadDefaultItems()
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
            loadDefaultItems() // Reuse the function to load popular items
            binding.categorySpinner.setSelection(0) // Reset spinner to "Select Category"
        }
    }

    private fun updateProductList(items: MutableList<ItemModel>) {
        adminViewProductAdapter.updateItems(items)
    }

    // *** NEW FUNCTION: To load popular items by default ***
    private fun loadDefaultItems() {
        viewModel.loadPopular().observe(viewLifecycleOwner) { popList ->
            updateProductList(popList.toMutableList())
        }
    }

    private fun navigateToProductDetails(item: ItemModel) {
        val detailsFragment = AdminProductDetailsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(AdminProductDetailsFragment.ARG_ITEM_OBJECT, item)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailsFragment)
            .addToBackStack(null)
            .commit()
    }

    // Refresh the list when returning from the details/update screen
    override fun onResume() {
        super.onResume()
        // Ensure that when you come back, the list is still populated based on the last state
        // or just reload popular by default for simplicity if the spinner state is complex to manage.
        reloadCurrentCategoryItems() // This function already handles re-loading based on spinner or popular
    }

    private fun reloadCurrentCategoryItems() {
        val selectedPosition = binding.categorySpinner.selectedItemPosition
        if (selectedPosition > 0) {
            val selectedCategory = categories[selectedPosition - 1]
            selectedCategory.id?.let { categoryId ->
                viewModel.loadItems(categoryId.toString()).observe(viewLifecycleOwner) { items ->
                    updateProductList(items)
                }
            }
        } else {
            // If "Select Category" is chosen or no category was previously selected, load popular items
            loadDefaultItems()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}