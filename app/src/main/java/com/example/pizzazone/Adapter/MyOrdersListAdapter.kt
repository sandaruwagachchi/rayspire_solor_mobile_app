package com.example.pizzazone.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzazone.Domain.OrderDetail
import com.example.pizzazone.R
import android.util.Log


class OrderListAdapter(
    private val orderList: ArrayList<OrderDetail>,
    private val itemLayoutResId: Int,
    private val orderStatusButtonClickListener: ((OrderDetail, Int) -> Unit)? = null

) : RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemLayoutResId, parent, false)
        return OrderViewHolder(view, orderStatusButtonClickListener)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orderList[position]


        holder.orderId.text = " ${currentOrder.orderId ?: "N/A"}"


        holder.totalAmount.text = "$${String.format("%.2f", currentOrder.totalAmount)}"


        holder.currentAddress.text = "${currentOrder.fullName}, ${currentOrder.address}, ${currentOrder.zipCode}"


        holder.date.text = "Date: ${currentOrder.date}"
        holder.time.text = "Time: ${currentOrder.time}"


        val itemsStringBuilder = StringBuilder()
        if (currentOrder.items.isNotEmpty()) {
            for (item in currentOrder.items) {
                itemsStringBuilder.append("${item.itemName} x${item.quantity}\n")
            }

            if (itemsStringBuilder.lastOrNull() == '\n') {
                itemsStringBuilder.deleteCharAt(itemsStringBuilder.length - 1)
            }
        } else {
            itemsStringBuilder.append("No items listed.")
        }
        holder.orderItems.text = itemsStringBuilder.toString()


        if (holder.statusButton != null) {

            if (currentOrder.status == "Done") {
                holder.statusButton.text = "Done"
                holder.statusButton.isEnabled = false


            } else {
                holder.statusButton.text = "Order"
                holder.statusButton.isEnabled = true

            }

            holder.statusButton.tag = currentOrder
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderViewHolder(
        itemView: View,
        orderStatusButtonClickListener: ((OrderDetail, Int) -> Unit)?
    ) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.tv_order_id)
        val totalAmount: TextView = itemView.findViewById(R.id.tv_total_amount)
        val orderItems: TextView = itemView.findViewById(R.id.tv_order_items)
        val currentAddress: TextView = itemView.findViewById(R.id.tv_current_address)
        val date: TextView = itemView.findViewById(R.id.tv_order_date)
        val time: TextView = itemView.findViewById(R.id.tv_order_time)
        val statusButton: Button? = itemView.findViewById(R.id.btn_order_status)

        init {

            statusButton?.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    val clickedOrder = it.tag as? OrderDetail
                    clickedOrder?.let { orderDetail ->
                        orderStatusButtonClickListener?.invoke(orderDetail, position)
                    } ?: Log.e("OrderViewHolder", "OrderDetail not found in button tag.")
                }
            }
        }
    }

    fun updateOrders(newOrders: List<OrderDetail>) {
        Log.d("OrderListAdapter", "Updating adapter with ${newOrders.size} new orders.")
        orderList.clear()
        orderList.addAll(newOrders)
        notifyDataSetChanged()
        Log.d("OrderListAdapter", "Adapter's internal list size after update: ${orderList.size}")
    }
}