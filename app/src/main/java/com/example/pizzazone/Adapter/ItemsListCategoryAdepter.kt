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
import com.example.pizzazone.databinding.ViewHolderItemPicLeftBinding
import com.example.pizzazone.databinding.ViewHolderItemPicRightBinding

class ItemsListCategoryAdapter(private val items: MutableList<ItemModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ITEM1 = 0 // Item with pic on right
        const val TYPE_ITEM2 = 1 // Item with pic on left
    }

    private lateinit var context: Context

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) TYPE_ITEM1 else TYPE_ITEM2
    }

    class ViewholderItem(val binding: ViewHolderItemPicRightBinding) : RecyclerView.ViewHolder(binding.root)
    class ViewholderItem2(val binding: ViewHolderItemPicLeftBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            TYPE_ITEM1 -> {
                val binding = ViewHolderItemPicRightBinding.inflate(
                    LayoutInflater.from(context),
                    parent, false
                )
                ViewholderItem(binding)
            }
            TYPE_ITEM2 -> {
                val binding = ViewHolderItemPicLeftBinding.inflate(
                    LayoutInflater.from(context),
                    parent, false
                )
                ViewholderItem2(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is ViewholderItem -> {
                holder.binding.textView7.text = item.title
                holder.binding.textView8.text = "$${String.format("%.2f", item.price)}" // Format price
                holder.binding.ratingBar.rating = item.rating.toFloat()

                if (item.picUrl.isNotEmpty()) {
                    Glide.with(context)
                        .load(item.picUrl[0])
                        .into(holder.binding.imageView4)
                } else {
                    Glide.with(context).load(R.drawable.placeholder_image).into(holder.binding.imageView4)
                }

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, DetailsScreenActivity::class.java)
                    intent.putExtra("object", item)
                    context.startActivity(intent)
                }
            }

            is ViewholderItem2 -> {
                holder.binding.textView7.text = item.title
                holder.binding.textView8.text = "$${String.format("%.2f", item.price)}" // Fixed price binding
                holder.binding.ratingBar.rating = item.rating.toFloat()

                if (item.picUrl.isNotEmpty()) {
                    Glide.with(context)
                        .load(item.picUrl[0])
                        .into(holder.binding.imageView4)
                } else {
                    Glide.with(context).load(R.drawable.placeholder_image).into(holder.binding.imageView4)
                }

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, DetailsScreenActivity::class.java)
                    intent.putExtra("object", item)
                    context.startActivity(intent)
                }
            }
        }
    }
}