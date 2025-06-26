package com.example.pizzazone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzazone.adapter.OrderAdapter
import com.example.pizzazone.model.Order

class AdminOrderHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_order_history)

        recyclerView = findViewById(R.id.order_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        val orderList = listOf(
            Order("01", "Colombo", "$15.00", "2025-06-25"),
            Order("02", "Galle", "$25.00", "2025-06-24"),
            Order("03", "Kandy", "$30.00", "2025-06-23")
        )

        adapter = OrderAdapter(orderList)
        recyclerView.adapter = adapter
    }
}
