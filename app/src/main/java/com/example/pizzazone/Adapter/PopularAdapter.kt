package com.example.pizzazone.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.DetailsScreenActivity
import com.example.pizzazone.R
import com.example.pizzazone.databinding.ViewholderPopularBinding
import com.example.pizzazone.CartManager // Import CartManager

class PopularAdapter(val items:MutableList<ItemModel>):RecyclerView.Adapter<PopularAdapter.Viewholder>() {
    lateinit var context: Context

    class Viewholder(val binding: ViewholderPopularBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularAdapter.Viewholder {
        context = parent.context
        val binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: PopularAdapter.Viewholder, position: Int) {
        val item = items[position]

        holder.binding.textView5.text = item.title
        holder.binding.textView6.text ="$"+item.price.toString()

        if (item.picUrl.isNotEmpty()) {
            Glide.with(context)
                .load(item.picUrl[0])
                .into(holder.binding.imageView3)
        } else {
            Glide.with(context).load(R.drawable.placeholder_image).into(holder.binding.imageView3)
        }

        // Handle item click to go to details screen
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailsScreenActivity::class.java)
            intent.putExtra(DetailsScreenActivity.EXTRA_ITEM_OBJECT, item)
            context.startActivity(intent)
        }

        // *** ADD TO CART BUTTON CLICK LISTENER ***
        holder.binding.imageView5.setOnClickListener { // imageView5 is your plus icon
            CartManager.addItemToCart(item)
            // Optionally, show a toast or a small animation to confirm addition
            // Toast.makeText(context, "${item.title} added to cart!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = items.size

    // *** NEW FUNCTION ADDED FOR SEARCH IMPLEMENTATION ***
    fun updateItems(newItems: List<ItemModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}