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
import com.example.pizzazone.CartManager

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

        val fallbackAsset = com.example.pizzazone.ImageFallbackProvider.getAssetImageForTitle(item.title)
        if (item.picUrl.isNotEmpty()) {
            Glide.with(context)
                .load(item.picUrl[0])
                .into(holder.binding.imageView3)
        } else if (fallbackAsset != null) {
            Glide.with(context)
                .load(fallbackAsset)
                .into(holder.binding.imageView3)
        } else {
            Glide.with(context).load(R.drawable.placeholder_image).into(holder.binding.imageView3)
        }


        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailsScreenActivity::class.java)
            intent.putExtra(DetailsScreenActivity.EXTRA_ITEM_OBJECT, item)
            context.startActivity(intent)
        }


        holder.binding.imageView5.setOnClickListener {
            CartManager.addItemToCart(item)

        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ItemModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}