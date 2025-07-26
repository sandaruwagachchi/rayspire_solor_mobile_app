package com.example.pizzazone

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pizzazone.Adapter.AdminDeleteProductAdapter
import com.example.pizzazone.Domain.CategoryModel
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.ViewModel.MainViewModel
import com.example.pizzazone.databinding.FragmentAdminDeleteProductBinding

class AdminDeleteProductFragment : Fragment() {

    private var _binding: FragmentAdminDeleteProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adminDeleteProductAdapter: AdminDeleteProductAdapter
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


        loadDefaultItems()

        return binding.root
    }

    private fun setupRecyclerView() {
        adminDeleteProductAdapter = AdminDeleteProductAdapter(mutableListOf()) { itemToDelete ->
            confirmAndDeleteItem(itemToDelete)
        }
        binding.listView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = adminDeleteProductAdapter
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
        adminDeleteProductAdapter.updateItems(items)
    }

    // *** NEW FUNCTION: To load popular items by default ***
    private fun loadDefaultItems() {
        viewModel.loadPopular().observe(viewLifecycleOwner) { popList ->
            updateProductList(popList.toMutableList())
        }
    }

    private fun confirmAndDeleteItem(item: ItemModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete '${item.title}'?")
            .setPositiveButton("Delete") { dialog, _ ->
                val itemId = item.id
                if (!itemId.isNullOrEmpty()) {
                    viewModel.deleteItem(itemId).observe(viewLifecycleOwner) { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(requireContext(), "${item.title} deleted successfully!", Toast.LENGTH_SHORT).show()
                            reloadCurrentCategoryItems()
                        } else {
                            Toast.makeText(requireContext(), "Failed to delete ${item.title}.", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }
                } else {
                    Toast.makeText(requireContext(), "Item ID not found for deletion.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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