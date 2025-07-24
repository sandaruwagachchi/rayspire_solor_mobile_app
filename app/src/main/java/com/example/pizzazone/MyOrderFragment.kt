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
import com.example.pizzazone.Adapter.OrderListAdapter // Changed from MyOrdersListAdapter to OrderListAdapter
import com.example.pizzazone.Domain.OrderDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Log

class MyOrderFragment : Fragment() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var orderListAdapter: OrderListAdapter // Changed adapter type
    private lateinit var orderList: ArrayList<OrderDetail>
    private lateinit var emptyOrdersTextView: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myorder, container, false)

        val backArrow = view.findViewById<ImageView>(R.id.shipping_back_arrow)
        emptyOrdersTextView = view.findViewById(R.id.emptyOrdersTextView)

        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        orderList = ArrayList()

        // Initialize OrderListAdapter with item_order.xml (no button click listener needed here)
        orderListAdapter = OrderListAdapter(orderList, R.layout.item_order, null) // Pass null for click listener
        ordersRecyclerView.adapter = orderListAdapter

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        backArrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        fetchUserOrdersFromFirebase()

        return view
    }

    private fun fetchUserOrdersFromFirebase() {
        val currentUser = auth.currentUser
        Log.d("MyOrderFragment", "Fetching orders initiated. Current Firebase User: ${currentUser?.email} (UID: ${currentUser?.uid})")

        if (currentUser == null) {
            Toast.makeText(requireContext(), "You need to be logged in to view your orders.", Toast.LENGTH_LONG).show()
            emptyOrdersTextView.visibility = View.VISIBLE
            ordersRecyclerView.visibility = View.GONE
            Log.d("MyOrderFragment", "User not logged in. Aborting order fetch.")
            return
        }

        val userId = currentUser.uid
        val ordersRef = database.getReference("Orders")

        Log.d("MyOrderFragment", "Querying orders for user ID: $userId")

        ordersRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fetchedOrderList = ArrayList<OrderDetail>()

                    if (snapshot.exists()) {
                        Log.d("MyOrderFragment", "Orders snapshot exists for user $userId. Number of children: ${snapshot.childrenCount}")
                        if (snapshot.childrenCount > 0) {
                            for (orderSnapshot in snapshot.children) {
                                val order = orderSnapshot.getValue(OrderDetail::class.java)
                                order?.let {
                                    fetchedOrderList.add(it)
                                    Log.d("MyOrderFragment", "Added order: ${it.orderId} (Total: ${it.totalAmount}), Items: ${it.items.size}")
                                } ?: run {
                                    Log.e("MyOrderFragment", "Failed to parse OrderDetail from snapshot: ${orderSnapshot.key}. Data: ${orderSnapshot.value}")
                                    Toast.makeText(requireContext(), "Error loading one or more orders. Check logs.", Toast.LENGTH_SHORT).show()
                                }
                            }

                            orderListAdapter.updateOrders(fetchedOrderList)
                            Log.d("MyOrderFragment", "Called adapter.updateOrders() with list of size: ${fetchedOrderList.size}")

                            if (fetchedOrderList.isNotEmpty()) {
                                emptyOrdersTextView.visibility = View.GONE
                                ordersRecyclerView.visibility = View.VISIBLE
                                Log.d("MyOrderFragment", "Orders displayed. Total orders now visible: ${fetchedOrderList.size}")
                            } else {
                                emptyOrdersTextView.visibility = View.VISIBLE
                                ordersRecyclerView.visibility = View.GONE
                                Toast.makeText(requireContext(), "No orders found for this user.", Toast.LENGTH_SHORT).show()
                                Log.d("MyOrderFragment", "No orders found for user ID: $userId after processing (should not happen if childrenCount > 0).")
                            }

                        } else {
                            orderListAdapter.updateOrders(emptyList())
                            emptyOrdersTextView.visibility = View.VISIBLE
                            ordersRecyclerView.visibility = View.GONE
                            Toast.makeText(requireContext(), "No orders found for this user.", Toast.LENGTH_SHORT).show()
                            Log.d("MyOrderFragment", "Snapshot exists but no matching orders found for user ID: $userId")
                        }
                    } else {
                        orderListAdapter.updateOrders(emptyList())
                        emptyOrdersTextView.visibility = View.VISIBLE
                        ordersRecyclerView.visibility = View.GONE
                        Toast.makeText(requireContext(), "No orders found for this user.", Toast.LENGTH_SHORT).show()
                        Log.d("MyOrderFragment", "No snapshot found for orders for user ID: $userId")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load orders: ${error.message}", Toast.LENGTH_LONG).show()
                    emptyOrdersTextView.visibility = View.VISIBLE
                    ordersRecyclerView.visibility = View.GONE
                    Log.e("MyOrderFragment", "Database error loading orders: ${error.message}", error.toException())
                }
            })
    }
}