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

// orderStatusButtonClickListener: button click එක handle කිරීමට (ReceivedOrderFragment සඳහා)
// itemLayoutResId: භාවිතා කරන layout file එක (item_order.xml හෝ item_received_order.xml)
class OrderListAdapter(
    private val orderList: ArrayList<OrderDetail>,
    private val itemLayoutResId: Int,
    private val orderStatusButtonClickListener: ((OrderDetail, Int) -> Unit)? = null // Nullable lambda for the click listener
) : RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemLayoutResId, parent, false)
        return OrderViewHolder(view, orderStatusButtonClickListener)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orderList[position]

        // Set order ID, ensuring it's not null
        holder.orderId.text = " ${currentOrder.orderId ?: "N/A"}"

        // Format total amount to 2 decimal places with a dollar sign
        holder.totalAmount.text = "$${String.format("%.2f", currentOrder.totalAmount)}"

        // Combine address details
        holder.currentAddress.text = "${currentOrder.fullName}, ${currentOrder.address}, ${currentOrder.zipCode}"

        // Set date and time
        holder.date.text = "Date: ${currentOrder.date}"
        holder.time.text = "Time: ${currentOrder.time}"

        // Build the items list string
        val itemsStringBuilder = StringBuilder()
        if (currentOrder.items.isNotEmpty()) {
            for (item in currentOrder.items) {
                itemsStringBuilder.append("${item.itemName} x${item.quantity}\n")
            }
            // Remove the last newline character if it exists
            if (itemsStringBuilder.lastOrNull() == '\n') {
                itemsStringBuilder.deleteCharAt(itemsStringBuilder.length - 1)
            }
        } else {
            itemsStringBuilder.append("No items listed.")
        }
        holder.orderItems.text = itemsStringBuilder.toString()

        // Handle the status button visibility and text
        if (holder.statusButton != null) {
            // Check the status of the order and set button text accordingly
            if (currentOrder.status == "Done") {
                holder.statusButton.text = "Done"
                holder.statusButton.isEnabled = false // Disable button if already Done
                // Optionally change button color when done
                // holder.statusButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray)));
            } else {
                holder.statusButton.text = "Order"
                holder.statusButton.isEnabled = true
                // Optionally reset button color when active
                // holder.statusButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.your_green_color)));
            }
            // Pass the entire OrderDetail object to the ViewHolder's button click listener
            // so the listener knows which order was clicked.
            holder.statusButton.tag = currentOrder
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderViewHolder(
        itemView: View,
        orderStatusButtonClickListener: ((OrderDetail, Int) -> Unit)? // Click listener passed to ViewHolder
    ) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.tv_order_id)
        val totalAmount: TextView = itemView.findViewById(R.id.tv_total_amount)
        val orderItems: TextView = itemView.findViewById(R.id.tv_order_items)
        val currentAddress: TextView = itemView.findViewById(R.id.tv_current_address)
        val date: TextView = itemView.findViewById(R.id.tv_order_date)
        val time: TextView = itemView.findViewById(R.id.tv_order_time)
        val statusButton: Button? = itemView.findViewById(R.id.btn_order_status) // This can be null if not present in layout

        init {
            // Set up the click listener for the status button if it exists
            statusButton?.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Retrieve the OrderDetail object from the button's tag
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