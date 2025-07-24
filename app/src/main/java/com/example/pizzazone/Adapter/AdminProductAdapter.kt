package com.example.pizzazone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.R
import com.example.pizzazone.databinding.ViewholderDeleteBinding // Correct binding for viewholder_delete.xml

class AdminDeleteProductAdapter(
    private var items: MutableList<ItemModel>,
    private val onDeleteClickListener: (ItemModel) -> Unit // Callback for delete button click
) : RecyclerView.Adapter<AdminDeleteProductAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(val binding: ViewholderDeleteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        // Inflate viewholder_delete.xml
        val binding = ViewholderDeleteBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textView5.text = item.title // Assuming textView5 for title
        holder.binding.textView6.text = "$${item.price}" // Assuming textView6 for price

        if (item.picUrl.isNotEmpty()) {
            Glide.with(context)
                .load(item.picUrl[0])
                .into(holder.binding.imageView3) // Assuming imageView3 for image
        } else {
            holder.binding.imageView3.setImageResource(R.drawable.placeholder_image) // Make sure you have this
        }

        // Set click listener for the delete icon (imageView5)
        holder.binding.imageView5.setOnClickListener {
            onDeleteClickListener.invoke(item) // Trigger the callback with the item to delete
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: MutableList<ItemModel>) {
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }
}