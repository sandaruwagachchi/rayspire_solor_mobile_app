package com.example.pizzazone.Domain







    data class OrderDetail(
        val orderId: String = "",
        val userId: String = "",
        val date: String = "",
        val time: String = "",
        val fullName: String = "",
        val address: String = "",
        val mobile: String = "",
        val zipCode: String = "",
        val totalAmount: Double = 0.0,
        val items: List<OrderItem> = emptyList()
    )




