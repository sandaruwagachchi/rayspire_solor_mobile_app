package com.example.pizzazone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzazone.Adapter.OrderListAdapter // MyOrdersListAdapter සිට OrderListAdapter ලෙස වෙනස් කර ඇත
import com.example.pizzazone.Domain.OrderDetail
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Log

class RecivedOrderFragment : Fragment() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var orderListAdapter: OrderListAdapter
    private lateinit var orderList: ArrayList<OrderDetail>
    private lateinit var emptyOrdersTextView: TextView
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_recived_order, container, false)

        val backArrow = view.findViewById<ImageView>(R.id.shipping_back_arrow)
        emptyOrdersTextView = view.findViewById(R.id.emptyOrdersTextView)

        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        orderList = ArrayList()


        orderListAdapter = OrderListAdapter(orderList, R.layout.item_received_order) { order, position ->
            handleOrderStatusChange(order, position)
        }
        ordersRecyclerView.adapter = orderListAdapter

        database = FirebaseDatabase.getInstance()

        backArrow.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        fetchAllOrders()

        return view
    }

    private fun fetchAllOrders() {
        val ordersRef = database.getReference("Orders")

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = ArrayList<OrderDetail>()

                if (snapshot.exists()) {
                    Log.d("FirebaseData", "All Orders Snapshot exists. Number of children: ${snapshot.childrenCount}")
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderDetail::class.java)
                        order?.let {
                            tempList.add(it)
                            Log.d("FirebaseData", "All Orders: Order added: ${it.orderId}, Total: ${it.totalAmount}")
                        } ?: Log.e("FirebaseData", "Failed to parse OrderDetail for snapshot: ${orderSnapshot.key}")
                    }
                    orderListAdapter.updateOrders(tempList)
                    emptyOrdersTextView.visibility = View.GONE
                    ordersRecyclerView.visibility = View.VISIBLE
                } else {
                    Log.d("FirebaseData", "No orders found in Firebase.")
                    emptyOrdersTextView.visibility = View.VISIBLE
                    ordersRecyclerView.visibility = View.GONE
                    Toast.makeText(requireContext(), "No orders found.", Toast.LENGTH_SHORT).show()
                    orderListAdapter.updateOrders(emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseData", "Failed to load orders: ${error.message}", error.toException())
                Toast.makeText(requireContext(), "Failed to load orders: ${error.message}", Toast.LENGTH_LONG).show()
                emptyOrdersTextView.visibility = View.VISIBLE
                ordersRecyclerView.visibility = View.GONE
            }
        })
    }


    private fun handleOrderStatusChange(order: OrderDetail, position: Int) {
        val orderId = order.orderId
        if (orderId == null) {
            Toast.makeText(requireContext(), "Order ID is missing, cannot process.", Toast.LENGTH_SHORT).show()
            return
        }


        val updatedOrder = order.copy(status = "Done")


        val receivedOrdersRef = database.getReference("Received_Orders").child(orderId)

        receivedOrdersRef.setValue(updatedOrder)
            .addOnSuccessListener {
                Log.d("ReceivedOrderFragment", "Order ${orderId} moved to Received_Orders with status Done.")
                Toast.makeText(requireContext(), "Order ${orderId} marked as Done!", Toast.LENGTH_SHORT).show()


                val originalOrderRef = database.getReference("Orders").child(orderId)
                originalOrderRef.child("status").setValue("Done")
                    .addOnSuccessListener {
                        Log.d("ReceivedOrderFragment", "Status of order ${orderId} updated to Done in original 'Orders' table.")


                        if (position != RecyclerView.NO_POSITION && position < orderList.size) {
                            orderList[position].status = "Done"
                            orderListAdapter.notifyItemChanged(position)
                        } else {
                            Log.w("ReceivedOrderFragment", "Attempted to update item at invalid position: $position")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ReceivedOrderFragment", "Failed to update status in original 'Orders' table for ${orderId}: ${e.message}", e)
                        Toast.makeText(requireContext(), "Failed to update original order status: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("ReceivedOrderFragment", "Failed to move order ${orderId} to Received_Orders: ${e.message}", e)
                Toast.makeText(requireContext(), "Failed to process order: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}