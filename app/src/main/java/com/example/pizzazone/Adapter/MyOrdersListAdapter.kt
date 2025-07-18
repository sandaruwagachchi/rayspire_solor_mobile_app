package com.example.pizzazone.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzazone.Domain.OrderDetail
import com.example.pizzazone.R

class MyOrdersListAdapter(private val orderList: ArrayList<OrderDetail>) :
    RecyclerView.Adapter<MyOrdersListAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orderList[position]

        holder.orderId.text = currentOrder.orderId
        holder.totalAmount.text = "$${String.format("%.2f", currentOrder.totalAmount)}"

        holder.currentAddress.text = "${currentOrder.fullName}, ${currentOrder.address}"
        holder.date.text = "Date: ${currentOrder.date}"
        holder.time.text = "Time: ${currentOrder.time}"


        val itemsStringBuilder = StringBuilder()
        if (currentOrder.items.isNotEmpty()) {
            for (item in currentOrder.items) {
                itemsStringBuilder.append("${item.itemName} x${item.quantity}\n")
            }

            if (itemsStringBuilder.last() == '\n') {
                itemsStringBuilder.deleteCharAt(itemsStringBuilder.length - 1)
            }
        } else {
            itemsStringBuilder.append("No items listed.")
        }
        holder.orderItems.text = itemsStringBuilder.toString()
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.tv_order_id)
        val totalAmount: TextView = itemView.findViewById(R.id.tv_total_amount)
        val orderItems: TextView = itemView.findViewById(R.id.tv_order_items)
        val currentAddress: TextView = itemView.findViewById(R.id.tv_current_address)
        val date: TextView = itemView.findViewById(R.id.tv_order_date)
        val time: TextView = itemView.findViewById(R.id.tv_order_time)
    }


    fun updateOrders(newOrders: List<OrderDetail>) {
        orderList.clear()
        orderList.addAll(newOrders)
        notifyDataSetChanged()
    }
}