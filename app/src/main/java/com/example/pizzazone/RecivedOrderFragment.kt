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

class RecivedOrderFragment : Fragment() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var myOrdersListAdapter: MyOrdersListAdapter
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
        myOrdersListAdapter = MyOrdersListAdapter(orderList)
        ordersRecyclerView.adapter = myOrdersListAdapter


        database = FirebaseDatabase.getInstance()

        backArrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        fetchAllOrdersFromFirebase()

        return view
    }

    private fun fetchAllOrdersFromFirebase() {
        val ordersRef = database.getReference("Orders")


        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()

                if (snapshot.exists()) {
                    Log.d("FirebaseData", "All Orders Snapshot exists. Number of children: ${snapshot.childrenCount}")
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderDetail::class.java)
                        order?.let {
                            orderList.add(it)
                            Log.d("FirebaseData", "All Orders: Order added: ${it.orderId}, Total: ${it.totalAmount}")
                        } ?: Log.e("FirebaseData", "Failed to parse OrderDetail for snapshot: ${orderSnapshot.key}")
                    }
                    myOrdersListAdapter.updateOrders(orderList)
                    emptyOrdersTextView.visibility = View.GONE
                } else {
                    Log.d("FirebaseData", "No orders found in Firebase.")
                    emptyOrdersTextView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "No orders found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseData", "Failed to load orders: ${error.message}", error.toException())
                Toast.makeText(requireContext(), "Failed to load orders: ${error.message}", Toast.LENGTH_LONG).show()
                emptyOrdersTextView.visibility = View.VISIBLE
            }
        })
    }
}