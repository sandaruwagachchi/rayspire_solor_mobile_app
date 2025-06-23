package com.example.pizzazone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.databinding.ViewholderPopularBinding


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
       holder.binding.textView5.text = items[position].title
        holder.binding.textView6.text ="$"+items[position].price.toString()

        Glide.with(context)
            .load(items[position].picUrl[0])
            .into(holder.binding.imageView3)
    }

    override fun getItemCount(): Int = items.size
}