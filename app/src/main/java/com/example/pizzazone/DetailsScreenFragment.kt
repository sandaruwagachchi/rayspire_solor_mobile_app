package com.example.pizzazone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.databinding.FragmentDetailsScreenBinding
import com.example.pizzazone.CartManager // Import CartManager

class DetailsScreenFragment : Fragment() {

    private var _binding: FragmentDetailsScreenBinding? = null
    private val binding get() = _binding!!

    private var itemModel: ItemModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            itemModel = it.getSerializable(DetailsScreenActivity.EXTRA_ITEM_OBJECT) as? ItemModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDetailsScreenBinding.inflate(inflater, container, false)
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

            // *** MODIFIED ADD TO CART BUTTON LISTENER ***
            binding.buttondetails.setOnClickListener {
                // 1. Add the current item to the cart using CartManager
                CartManager.addItemToCart(item)

                // 2. Navigate to the CartFragment via the BottomNavigationView
                // This assumes DetailsScreenActivity hosts the BottomNavigationView.
                // It's good practice to ensure the activity is of the correct type.
                (activity as? HomeScreenActivity)?.let { homeScreenActivity ->
                    homeScreenActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.nav_cart
                }

                // Optionally, if you want to close the DetailsScreenActivity after adding to cart
                 requireActivity().finish()
            }

        } ?: run {
            // Handle case where itemModel is null (e.g., show an error message)
            binding.textProductName.text = "Error: Product details not found."
            // Hide other UI elements if they depend on itemModel
            binding.textProductDescription.visibility = View.GONE
            binding.textProductPrice.visibility = View.GONE
            binding.imageProduct.visibility = View.GONE
            binding.buttondetails.visibility = View.GONE
            binding.textDelivery.visibility = View.GONE
            binding.detailsTitle.visibility = View.GONE // If you want to hide this as well
            binding.scrollDescription.visibility = View.GONE // If you want to hide this as well
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}