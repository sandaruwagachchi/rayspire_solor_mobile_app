package com.example.pizzazone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzazone.R
import com.example.pizzazone.model.Order

class OrderAdapter(private val orderList: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId: TextView = itemView.findViewById(R.id.tv_order_id)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_order_address)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_order_price)
        val tvDate: TextView = itemView.findViewById(R.id.tv_order_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_row, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.tvId.text = order.id
        holder.tvAddress.text = order.address
        holder.tvPrice.text = order.price
        holder.tvDate.text = order.date
    }

    override fun getItemCount(): Int = orderList.size
}
