// src/main/java/com/example/pizzazone/Adapter/AdminViewProductAdapter.kt
package com.example.pizzazone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.R
import com.example.pizzazone.databinding.ViewholderPopularBinding

class AdminViewProductAdapter(
    private var items: MutableList<ItemModel>,
    private val onItemClickListener: (ItemModel) -> Unit // Callback for item click
) : RecyclerView.Adapter<AdminViewProductAdapter.ViewHolder>() {

    private lateinit var context: Context

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
            holder.binding.imageView3.setImageResource(R.drawable.placeholder_image)
        }


        holder.itemView.setOnClickListener {
            onItemClickListener.invoke(item) // Trigger the callback
        }

        holder.binding.imageView5.visibility = View.GONE
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: MutableList<ItemModel>) {
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }
}