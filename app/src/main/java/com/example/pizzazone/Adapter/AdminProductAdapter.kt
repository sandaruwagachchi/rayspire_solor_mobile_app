package com.example.pizzazone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.R
import com.example.pizzazone.databinding.ViewholderPopularBinding // Reusing this layout for simplicity

class AdminProductAdapter(private var items: MutableList<ItemModel>) :
    RecyclerView.Adapter<AdminProductAdapter.ViewHolder>() {


    class ViewHolder(val binding: ViewholderPopularBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textView5.text = item.title
        holder.binding.textView6.text = "$${item.price}"

        if (item.picUrl.isNotEmpty()) {
            Glide.with(context)
                .load(item.picUrl[0])
                .into(holder.binding.imageView3)
        } else {
            // Handle case where picUrl is empty, e.g., set a placeholder image
            holder.binding.imageView3.setImageResource(R.drawable.placeholder_image) // You'll need to add a placeholder_image to your drawables
        }

        // Add a click listener for editing/viewing product details for admin
        holder.itemView.setOnClickListener {
            // TODO: Implement navigation to an Admin Product Detail/Edit screen
            // You might want to pass the ItemModel object to the next activity/fragment
            // val intent = Intent(context, AdminEditProductActivity::class.java)
            // intent.putExtra("product", item)
            // context.startActivity(intent)
            // For now, let's just log a message or show a toast
            // Toast.makeText(context, "Clicked: ${item.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = items.size

    // Method to update the list of items
    fun updateItems(newItems: MutableList<ItemModel>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}