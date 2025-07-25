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
    private lateinit var suggestionAdapter: SuggestionAdapter

    private var allItems: List<ItemModel> = emptyList()
    private var originalPopularItems: MutableList<ItemModel> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]


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
            layoutManager = GridLayoutManager(context, 2) // Set span count to 2
            adapter = popularAdapter
        }
        viewModel.loadPopular().observe(viewLifecycleOwner) { popList ->
            originalPopularItems.clear()
            originalPopularItems.addAll(popList)
            popularAdapter.updateItems(popList)
        }

        viewModel.loadAllItems().observe(viewLifecycleOwner) { items ->
            allItems = items // Store all items
        }


        suggestionAdapter = SuggestionAdapter(emptyList()) { item ->
            val intent = Intent(context, DetailsScreenActivity::class.java)
            intent.putExtra(DetailsScreenActivity.EXTRA_ITEM_OBJECT, item)
            startActivity(intent)

            binding.editTextText3.text.clear()
            binding.suggestionsRecyclerView.visibility = View.GONE
        }

        binding.suggestionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = suggestionAdapter
            visibility = View.GONE
        }


        binding.editTextText3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    filterSuggestions(query)
                } else {
                    binding.suggestionsRecyclerView.visibility = View.GONE

                    binding.recycleView2.visibility = View.VISIBLE
                    popularAdapter.updateItems(originalPopularItems)
                }
            }
        })


        binding.editTextText3.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && binding.suggestionsRecyclerView.visibility == View.VISIBLE) {

                v.postDelayed({
                    if (!v.isPressed) {
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
            binding.recycleView2.visibility = View.GONE
        } else {
            binding.suggestionsRecyclerView.visibility = View.GONE
            binding.recycleView2.visibility = View.VISIBLE
            popularAdapter.updateItems(originalPopularItems)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}