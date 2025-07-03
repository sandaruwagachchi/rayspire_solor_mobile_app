package com.example.pizzazone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide // For image loading
import com.example.pizzazone.Domain.ItemModel // Import ItemModel
import com.example.pizzazone.databinding.FragmentDetailsScreenBinding // Assuming your layout is named fragment_details_screen.xml

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


            binding.buttondetails.setOnClickListener {

                DetailsScreenActivity.bottomNavViewInstance?.selectedItemId = R.id.nav_cart

            }

        } ?: run {

            binding.textProductName.text = "Error: Product details not found."

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}