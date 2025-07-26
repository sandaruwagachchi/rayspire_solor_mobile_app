package com.example.pizzazone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzazone.Domain.ItemModel


data class CartItem(
    val item: ItemModel,
    var quantity: Int = 1
)

object CartManager {

    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> get() = _cartItems


    private val _cartItemCount = MutableLiveData(0)
    val cartItemCount: LiveData<Int> get() = _cartItemCount


    fun addItemToCart(itemModel: ItemModel) {
        val currentList = _cartItems.value ?: mutableListOf()
        val existingItem = currentList.find {
            (it.item.categoryId == itemModel.categoryId && it.item.title == itemModel.title)
        }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentList.add(CartItem(itemModel))
        }

        _cartItems.value = currentList
        updateCartItemCount()
    }


    fun decrementItemQuantity(itemModel: ItemModel) {
        val currentList = _cartItems.value ?: mutableListOf()
        val existingItem = currentList.find {
            (it.item.categoryId == itemModel.categoryId && it.item.title == itemModel.title)
        }

        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity--
            } else {
                currentList.remove(existingItem)
            }
        }
        _cartItems.value = currentList
        updateCartItemCount()
    }


    fun removeAllInstancesOfItem(itemModel: ItemModel) {
        val currentList = _cartItems.value ?: mutableListOf()
        val newList = currentList.filter {
            !(it.item.categoryId == itemModel.categoryId && it.item.title == itemModel.title)
        }.toMutableList()

        _cartItems.value = newList
        updateCartItemCount()
    }


    fun clearCart() {
        _cartItems.value = mutableListOf()
        updateCartItemCount()
    }


    private fun updateCartItemCount() {
        val totalCount = (_cartItems.value ?: mutableListOf()).sumOf { it.quantity }
        _cartItemCount.value = totalCount
    }
}