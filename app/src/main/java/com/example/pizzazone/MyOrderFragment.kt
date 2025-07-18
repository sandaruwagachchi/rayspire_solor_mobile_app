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
import com.example.pizzazone.Adapter.MyOrdersListAdapter
import com.example.pizzazone.Domain.OrderDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Log

class MyOrderFragment : Fragment() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var myOrdersListAdapter: MyOrdersListAdapter
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
        myOrdersListAdapter = MyOrdersListAdapter(orderList)
        ordersRecyclerView.adapter = myOrdersListAdapter

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
        if (currentUser == null) {
            Toast.makeText(requireContext(), "You need to be logged in to view your orders.", Toast.LENGTH_LONG).show()
            emptyOrdersTextView.visibility = View.VISIBLE
            Log.d("MyOrderFragment", "User not logged in.")
            return
        }

        val userId = currentUser.uid
        val ordersRef = database.getReference("Orders")

        Log.d("MyOrderFragment", "Fetching orders for user ID: $userId")


        ordersRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    orderList.clear()

                    if (snapshot.exists()) {
                        Log.d("MyOrderFragment", "Snapshot exists. Number of user orders: ${snapshot.childrenCount}")
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderDetail::class.java)
                            order?.let {
                                orderList.add(it)
                                Log.d("MyOrderFragment", "User Order added: ${it.orderId}, Total: ${it.totalAmount}")
                            } ?: Log.e("MyOrderFragment", "Failed to parse OrderDetail for snapshot: ${orderSnapshot.key}")
                        }
                        myOrdersListAdapter.updateOrders(orderList)
                        emptyOrdersTextView.visibility = View.GONE
                    } else {
                        emptyOrdersTextView.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "No orders found for this user.", Toast.LENGTH_SHORT).show()
                        Log.d("MyOrderFragment", "No orders found for user ID: $userId")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load orders: ${error.message}", Toast.LENGTH_LONG).show()
                    emptyOrdersTextView.visibility = View.VISIBLE
                    Log.e("MyOrderFragment", "Failed to load orders: ${error.message}", error.toException())
                }
            })
    }
}