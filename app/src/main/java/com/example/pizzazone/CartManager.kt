package com.example.pizzazone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzazone.Domain.ItemModel

// A data class to represent an item in the cart,
// including its quantity and a reference to the actual ItemModel
data class CartItem(
    val item: ItemModel,
    var quantity: Int = 1
)

object CartManager {
    // MutableLiveData to observe changes in the cart
    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> get() = _cartItems

    // MutableLiveData for total item count to update badge
    private val _cartItemCount = MutableLiveData(0)
    val cartItemCount: LiveData<Int> get() = _cartItemCount

    // Function to add an item to the cart
    fun addItemToCart(itemModel: ItemModel) {
        val currentList = _cartItems.value ?: mutableListOf()
        // Using `id` for unique identification if available, otherwise fallback to `title`
        val existingItem = currentList.find {
            (it.item.categoryId == itemModel.categoryId && it.item.title == itemModel.title) // More robust check
        }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentList.add(CartItem(itemModel))
        }

        _cartItems.value = currentList // Trigger LiveData update
        updateCartItemCount()
    }

    // Function to decrement quantity or remove if quantity is 1
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

    // *** NEW: Function to remove all instances of a specific item from the cart ***
    fun removeAllInstancesOfItem(itemModel: ItemModel) {
        val currentList = _cartItems.value ?: mutableListOf()
        val newList = currentList.filter {
            !(it.item.categoryId == itemModel.categoryId && it.item.title == itemModel.title)
        }.toMutableList() // Filter out the item to be removed

        _cartItems.value = newList
        updateCartItemCount()
    }

    // Function to clear the entire cart
    fun clearCart() {
        _cartItems.value = mutableListOf()
        updateCartItemCount()
    }

    // Helper to calculate and update the total count for the badge
    private fun updateCartItemCount() {
        val totalCount = (_cartItems.value ?: mutableListOf()).sumOf { it.quantity }
        _cartItemCount.value = totalCount
    }
}