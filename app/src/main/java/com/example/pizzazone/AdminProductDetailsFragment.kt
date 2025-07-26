// src/main/java/com/example/pizzazone/AdminProductDetailsFragment.kt
package com.example.pizzazone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.databinding.FragmentAdminProductDetailsBinding

class AdminProductDetailsFragment : Fragment() {

    private var _binding: FragmentAdminProductDetailsBinding? = null
    private val binding get() = _binding!!

    private var itemModel: ItemModel? = null

    companion object {
        const val ARG_ITEM_OBJECT = "item_object"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemModel = it.getSerializable(ARG_ITEM_OBJECT) as? ItemModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemModel?.let { item ->
            binding.textProductName.text = item.title
            binding.textProductDescription.text = item.description
            binding.textProductPrice.text = "$${String.format("%.2f", item.price)}"

            if (item.picUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(item.picUrl[0])
                    .into(binding.imageProduct)
            } else {
                binding.imageProduct.setImageResource(R.drawable.placeholder_image)
            }

            binding.backArrow.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            // This is the button that triggers navigation to the update form
            binding.buttonUpdate.setOnClickListener {
                navigateToUpdateScreen(item)
            }

        } ?: run {
            binding.textProductName.text = "Error: Product details not found."
            binding.textProductDescription.visibility = View.GONE
            binding.textProductPrice.visibility = View.GONE
            binding.imageProduct.visibility = View.GONE
            binding.buttonUpdate.visibility = View.GONE
            // ... hide any other relevant views
        }
    }

    private fun navigateToUpdateScreen(item: ItemModel) {
        val updateFragment = AdminUpdateProductFormFragment().apply {
            arguments = Bundle().apply {
                putSerializable(AdminUpdateProductFormFragment.ARG_ITEM_OBJECT_TO_UPDATE, item)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, updateFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}