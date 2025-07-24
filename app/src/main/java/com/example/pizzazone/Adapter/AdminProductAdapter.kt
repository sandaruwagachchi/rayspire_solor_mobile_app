package com.example.pizzazone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.R
import com.example.pizzazone.databinding.ViewholderDeleteBinding // Reusing this layout for simplicity

class AdminProductAdapter(private var items: MutableList<ItemModel>) :
    RecyclerView.Adapter<AdminProductAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(val binding: ViewholderDeleteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderDeleteBinding.inflate(LayoutInflater.from(context), parent, false)
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

        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: MutableList<ItemModel>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}