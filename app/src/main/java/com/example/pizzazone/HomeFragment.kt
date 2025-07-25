package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pizzazone.Adapter.CategoryAdapter
import com.example.pizzazone.Adapter.PopularAdapter
import com.example.pizzazone.Adapter.SuggestionAdapter // Import the new adapter
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.ViewModel.MainViewModel
import com.example.pizzazone.databinding.FragmentHomeBinding
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularAdapter: PopularAdapter
    private lateinit var suggestionAdapter: SuggestionAdapter // Declare suggestion adapter

    private var allItems: List<ItemModel> = emptyList() // To store all items from Firebase
    private var originalPopularItems: MutableList<ItemModel> = mutableListOf()

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

        // Setup popular items RecyclerView (grid)
        popularAdapter = PopularAdapter(mutableListOf())
        binding.recycleView2.apply {
            layoutManager = GridLayoutManager(context, 2) // Set span count to 2
            adapter = popularAdapter
        }
        viewModel.loadPopular().observe(viewLifecycleOwner) { popList ->
            originalPopularItems.clear()
            originalPopularItems.addAll(popList)
            popularAdapter.updateItems(popList)
        }

        // *** NEW: Load all items for search suggestions ***
        viewModel.loadAllItems().observe(viewLifecycleOwner) { items ->
            allItems = items // Store all items
        }

        // Setup search suggestion RecyclerView
        suggestionAdapter = SuggestionAdapter(emptyList()) { item ->
            // Handle click on a suggestion: navigate to item details
            val intent = Intent(context, DetailsScreenActivity::class.java)
            intent.putExtra(DetailsScreenActivity.EXTRA_ITEM_OBJECT, item)
            startActivity(intent)

            // Optional: Clear search bar and hide suggestions after clicking
            binding.editTextText3.text.clear()
            binding.suggestionsRecyclerView.visibility = View.GONE
        }

        binding.suggestionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = suggestionAdapter
            visibility = View.GONE // Start hidden
        }

        // Search bar implementation for suggestions
        binding.editTextText3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this implementation
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for this implementation
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    filterSuggestions(query)
                } else {
                    binding.suggestionsRecyclerView.visibility = View.GONE
                    // When search bar is empty, show popular items again if they were hidden
                    binding.recycleView2.visibility = View.VISIBLE
                    popularAdapter.updateItems(originalPopularItems)
                }
            }
        })

        // Add a focus change listener to hide suggestions when search bar loses focus
        binding.editTextText3.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && binding.suggestionsRecyclerView.visibility == View.VISIBLE) {
                // Delay hiding slightly to allow click event on suggestion to register
                v.postDelayed({
                    if (!v.isPressed) { // Check if not pressed (meaning a suggestion wasn't clicked)
                        binding.suggestionsRecyclerView.visibility = View.GONE
                    }
                }, 100)
            }
        }


        return binding.root
    }

    private fun filterSuggestions(query: String) {
        val filteredSuggestions = allItems.filter {
            it.title.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))
        }

        if (filteredSuggestions.isNotEmpty()) {
            suggestionAdapter.updateSuggestions(filteredSuggestions)
            binding.suggestionsRecyclerView.visibility = View.VISIBLE
            // Optional: Hide other RecyclerViews when suggestions are shown
            binding.recycleView2.visibility = View.GONE // Hide popular items
        } else {
            binding.suggestionsRecyclerView.visibility = View.GONE
            binding.recycleView2.visibility = View.VISIBLE // Show popular items if no suggestions
            popularAdapter.updateItems(originalPopularItems) // Reset popular items
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}