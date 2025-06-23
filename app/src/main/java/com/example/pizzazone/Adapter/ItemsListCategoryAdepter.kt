package com.example.pizzazone.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.DetailsScreenActivity // Assuming you have a DetailActivity for item details
import com.example.pizzazone.databinding.ViewHolderItemPicLeftBinding
import com.example.pizzazone.databinding.ViewHolderItemPicRightBinding

class ItemsListCategoryAdapter(val items: MutableList<ItemModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ITEM1 = 0
        const val TYPE_ITEM2 = 1
    }

    lateinit var context: Context
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
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        fun bindCommonData(
            titleTxt: String,
            priceTxt: String,
            rating: Float,
            picUrl: String
        ) {
            when (holder) {
                is ViewholderItem -> {
                    holder.binding.textView7.text = titleTxt
                    holder.binding.textView8.text = priceTxt
                    holder.binding.ratingBar.rating = rating

                    Glide.with(context)
                        .load(picUrl)
                        .into(holder.binding.imageView4)

                    holder.itemView.setOnClickListener {
                        val intent = Intent(context, DetailsScreenActivity::class.java) // Corrected: Start DetailActivity
                        intent.putExtra("object", items[position])
                        context.startActivity(intent)
                    }
                }

                is ViewholderItem2 -> {
                    holder.binding.textView7.text = titleTxt
                    holder.binding.textView8.text = priceTxt // Should be priceTxt not titleTxt
                    holder.binding.ratingBar.rating = rating

                    Glide.with(context)
                        .load(picUrl)
                        .into(holder.binding.imageView4)

                    holder.itemView.setOnClickListener {
                        val intent = Intent(context, DetailsScreenActivity::class.java) // Corrected: Start DetailActivity
                        intent.putExtra("object", items[position])
                        context.startActivity(intent)
                    }
                }
            }
        }

        bindCommonData(
            item.title,
            "${item.price} USD",
            item.rating.toFloat(),
            item.picUrl[0]
        )
    }
}