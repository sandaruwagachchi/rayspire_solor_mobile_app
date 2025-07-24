package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pizzazone.Domain.CategoryModel
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.ViewModel.MainViewModel
import com.example.pizzazone.databinding.FragmentAdminUpdateProductFormBinding
import com.bumptech.glide.Glide // Import Glide if you're using it

class AdminUpdateProductFormFragment : Fragment() {

    private var _binding: FragmentAdminUpdateProductFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    private var itemToUpdate: ItemModel? = null
    private var categories: MutableList<CategoryModel> = mutableListOf()
    private var selectedCategoryId: String? = null

    companion object {
        const val ARG_ITEM_OBJECT_TO_UPDATE = "item_object_to_update"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemToUpdate = it.getSerializable(ARG_ITEM_OBJECT_TO_UPDATE) as? ItemModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUpdateProductFormBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        Log.d("FORM_DEBUG", "ViewModel initialized in onCreateView.")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FORM_DEBUG", "onViewCreated called. Checking ViewModel state.")

        // Make sure these views exist in your XML:
        binding.headerLayout.visibility = View.VISIBLE
        binding.headerSeparator.visibility = View.VISIBLE
        binding.textView3.text = getString(R.string.update_product)

        // Call the setup functions (ensure they are defined below within the class)
        setupFormFields()
        setupCategorySpinner()
        setupClickListeners()
    }

    private fun setupFormFields() {
        itemToUpdate?.let { item ->
            binding.editTextTitle.setText(item.title) // Corrected: Should be editTextTitle now
            binding.editTextDescription.setText(item.description)
            binding.editTextPrice.setText(item.price.toString())

            // Load image using Glide
            if (item.picUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(item.picUrl[0]) // Access the first element of the ArrayList
                    .into(binding.imageViewProduct) // Corrected: Should be imageViewProduct now
            } else {
                // Set a placeholder image if picUrl is empty
                binding.imageViewProduct.setImageResource(R.drawable.placeholder_image) // Make sure you have this drawable
            }
        } ?: run {
            Toast.makeText(requireContext(), "Error: Product data not found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCategorySpinner() {
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

            // Select the item's current category in the spinner
            itemToUpdate?.categoryId?.let { currentCatId ->
                val index = categories.indexOfFirst { it.id.toString() == currentCatId }
                if (index != -1) {
                    binding.categorySpinner.setSelection(index + 1) // +1 because of "Select Category"
                }
            }

            binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position > 0) {
                        selectedCategoryId = categories[position - 1].id.toString()
                    } else {
                        selectedCategoryId = null // No category selected
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedCategoryId = null
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.buttonUpdateProduct.setOnClickListener { // Corrected: Should be buttonUpdateProduct now
            updateProduct()
        }

        binding.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun updateProduct() {
        val title = binding.editTextTitle.text.toString().trim() // Corrected: Should be editTextTitle now
        val description = binding.editTextDescription.text.toString().trim()
        val priceString = binding.editTextPrice.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || priceString.isEmpty() || selectedCategoryId == null) {
            Toast.makeText(requireContext(), "Please fill all fields and select a category.", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceString.toDoubleOrNull()
        if (price == null || price <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid price.", Toast.LENGTH_SHORT).show()
            return
        }

        // Ensure itemToUpdate is not null and has an ID
        itemToUpdate?.id?.let { itemId ->
            val updatedItem = ItemModel(
                id = itemId, // Keep the original ID
                title = title,
                description = description,
                price = price,
                categoryId = selectedCategoryId!!,
                picUrl = itemToUpdate?.picUrl ?: arrayListOf() // Keep original picUrl for now, or add logic for new image upload
            )

            viewModel.updateItem(updatedItem).observe(viewLifecycleOwner) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Product updated successfully!", Toast.LENGTH_SHORT).show()

                    // --- START MODIFICATION ---
                    // Replace YourTargetActivity::class.java with the actual Activity you want to navigate to.
                    // For example, if you want to go back to the Admin Main Activity:
                    val intent = Intent(requireActivity(), AdminHomeScreenActivity::class.java)

                    // Optional: Clear the back stack so the user can't go back to the update form
                    // This is often desired after a successful form submission.
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                    startActivity(intent)
                    requireActivity().finish() // Finish the current hosting Activity if you don't want to keep it in the stack
                    // --- END MODIFICATION ---
                } else {
                    Toast.makeText(requireContext(), "Failed to update product.", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(requireContext(), "Error: Original product data missing for update.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}