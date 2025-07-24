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

class RecivedOrderFragment : Fragment() { // Fragment නම ReceivedOrderFragment ලෙස නිවැරදි කර ඇත

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var orderListAdapter: OrderListAdapter // Adapter වර්ගය වෙනස් කර ඇත
    private lateinit var orderList: ArrayList<OrderDetail>
    private lateinit var emptyOrdersTextView: TextView
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_received_order.xml layout එක භාවිතා කරන්න (මෙම ගොනුවේ නම ද නිවැරදි විය යුතුය)
        val view = inflater.inflate(R.layout.fragment_recived_order, container, false)

        val backArrow = view.findViewById<ImageView>(R.id.shipping_back_arrow)
        emptyOrdersTextView = view.findViewById(R.id.emptyOrdersTextView)

        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        orderList = ArrayList()

        // OrderListAdapter initialize කිරීම.
        // item_received_order.xml layout එක සහ button click listener එක මෙහිදී ලබා දේ.
        orderListAdapter = OrderListAdapter(orderList, R.layout.item_received_order) { order, position ->
            handleOrderStatusChange(order, position)
        }
        ordersRecyclerView.adapter = orderListAdapter

        database = FirebaseDatabase.getInstance()

        backArrow.setOnClickListener {
            // HomeFragment වෙත නැවත යාමට
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
                val tempList = ArrayList<OrderDetail>() // නව දත්ත ලබා ගැනීමට තාවකාලික list එකක්

                if (snapshot.exists()) {
                    Log.d("FirebaseData", "All Orders Snapshot exists. Number of children: ${snapshot.childrenCount}")
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderDetail::class.java)
                        order?.let {
                            tempList.add(it)
                            Log.d("FirebaseData", "All Orders: Order added: ${it.orderId}, Total: ${it.totalAmount}")
                        } ?: Log.e("FirebaseData", "Failed to parse OrderDetail for snapshot: ${orderSnapshot.key}")
                    }
                    orderListAdapter.updateOrders(tempList) // ලබාගත් සියලු orders සමඟ adapter එක update කරන්න
                    emptyOrdersTextView.visibility = View.GONE // "No orders" message එක සඟවන්න
                    ordersRecyclerView.visibility = View.VISIBLE // RecyclerView එක පෙන්වන්න
                } else {
                    Log.d("FirebaseData", "No orders found in Firebase.")
                    emptyOrdersTextView.visibility = View.VISIBLE // "No orders" message එක පෙන්වන්න
                    ordersRecyclerView.visibility = View.GONE // RecyclerView එක සඟවන්න
                    Toast.makeText(requireContext(), "No orders found.", Toast.LENGTH_SHORT).show()
                    orderListAdapter.updateOrders(emptyList()) // orders නොමැති නම් adapter එක clear කරන්න
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseData", "Failed to load orders: ${error.message}", error.toException())
                Toast.makeText(requireContext(), "Failed to load orders: ${error.message}", Toast.LENGTH_LONG).show()
                emptyOrdersTextView.visibility = View.VISIBLE // error එකකදී "No orders" message එක පෙන්වන්න
                ordersRecyclerView.visibility = View.GONE // error එකකදී RecyclerView එක සඟවන්න
            }
        })
    }

    // Button click එක handle කිරීමට සහ Firebase update කිරීමට නව function එක
    private fun handleOrderStatusChange(order: OrderDetail, position: Int) {
        val orderId = order.orderId
        if (orderId == null) {
            Toast.makeText(requireContext(), "Order ID is missing, cannot process.", Toast.LENGTH_SHORT).show()
            return
        }

        // Order එකේ පිටපතක් සාදා එහි status එක "Done" ලෙස update කරන්න
        // Firebase විසින් data classes කෙලින්ම handle කරන නිසා toMap() අවශ්‍ය නොවේ.
        val updatedOrder = order.copy(status = "Done")

        // 1. දත්ත "Received_Orders" Table එකට "status: Done" ලෙස copy කරන්න
        val receivedOrdersRef = database.getReference("Received_Orders").child(orderId)

        receivedOrdersRef.setValue(updatedOrder) // කෙලින්ම OrderDetail object එක Firebase වෙත යවන්න
            .addOnSuccessListener {
                Log.d("ReceivedOrderFragment", "Order ${orderId} moved to Received_Orders with status Done.")
                Toast.makeText(requireContext(), "Order ${orderId} marked as Done!", Toast.LENGTH_SHORT).show()

                // 2. මුල් "Orders" Table එකේ status එක "Done" ලෙස update කරන්න
                val originalOrderRef = database.getReference("Orders").child(orderId)
                originalOrderRef.child("status").setValue("Done")
                    .addOnSuccessListener {
                        Log.d("ReceivedOrderFragment", "Status of order ${orderId} updated to Done in original 'Orders' table.")

                        // UI එකේ ක්ෂණිකව වෙනස්වීම් පෙන්වීමට local list එකේ status එක update කරන්න
                        if (position != RecyclerView.NO_POSITION && position < orderList.size) {
                            orderList[position].status = "Done" // local list එකේ status එක update කරන්න
                            orderListAdapter.notifyItemChanged(position) // අදාළ item එක නැවත render කිරීමට adapter එකට දන්වන්න
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
    // toMap() helper function එක මෙතනින් ඉවත් කර ඇත
}