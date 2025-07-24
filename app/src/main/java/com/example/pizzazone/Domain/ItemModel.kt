package com.example.pizzazone.Domain

import java.io.Serializable

data class ItemModel(
    var title: String = "",
    var description: String = "",
    var picUrl: ArrayList<String> = ArrayList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int? = null,
    var categoryId: String = "",
    var id: String? = null
) : Serializable