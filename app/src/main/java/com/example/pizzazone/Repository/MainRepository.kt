package com.example.pizzazone.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.Domain.CategoryModel
import com.google.firebase.database.*

class MainRepository {

    private val db = FirebaseDatabase.getInstance().reference

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        val data = MutableLiveData<MutableList<CategoryModel>>()
        db.child("Category").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                val list = mutableListOf<CategoryModel>()
                for (c in snap.children) {
                    c.getValue(CategoryModel::class.java)?.let { list.add(it) }
                }
                data.value = list
            }
            override fun onCancelled(err: DatabaseError) {
                // TODO: Handle error properly, e.g., log it or post null to LiveData
                // data.postValue(null) // Or post an empty list
                println("DatabaseError for Category: ${err.message}")
            }
        })
        return data
    }

    fun loadPopular(): LiveData<MutableList<ItemModel>> { // Changed to MutableList for consistency
        val data = MutableLiveData<MutableList<ItemModel>>() // Changed to MutableList
        db.child("Popular").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (c in snap.children) {
                    c.getValue(ItemModel::class.java)?.let { list.add(it) }
                }
                data.value = list
            }
            override fun onCancelled(err: DatabaseError) {
                // TODO: Handle error properly
                println("DatabaseError for Popular: ${err.message}")
            }
        })
        return data
    }

    fun loadItemCategory(categoryId: String): LiveData<MutableList<ItemModel>> {
        val itemsLiveData = MutableLiveData<MutableList<ItemModel>>()
        val ref = db.child("Items")
        val query: Query = ref.orderByChild("categoryId").equalTo(categoryId)

        // Using addListenerForSingleValueEvent to fetch data once
        // If you need real-time updates, use addValueEventListener
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (c in snapshot.children) {
                    c.getValue(ItemModel::class.java)?.let { list.add(it) }
                }
                itemsLiveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO: Handle error appropriately, e.g., log it or set an error state
                println("DatabaseError for ItemCategory ($categoryId): ${error.message}")
                itemsLiveData.value = mutableListOf() // Post an empty list on error
            }
        })
        return itemsLiveData
    }
}