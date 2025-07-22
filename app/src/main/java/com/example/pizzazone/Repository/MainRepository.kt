package com.example.pizzazone.Repository

import android.util.Log
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
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryModel>()
                for (itemSnapshot in snapshot.children) {
                    val category = itemSnapshot.getValue(CategoryModel::class.java)
                    category?.let {
                        list.add(it)
                    }
                }
                data.value = list
                Log.d("FIREBASE_CATEGORY", "Loaded ${list.size} categories")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_CATEGORY", "Error loading categories", error.toException())
                data.value = mutableListOf() // return empty list on error
            }
        })
        return data
    }

    fun loadPopular(): LiveData<MutableList<ItemModel>> {
        val data = MutableLiveData<MutableList<ItemModel>>()
        db.child("Popular").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (c in snapshot.children) {
                    c.getValue(ItemModel::class.java)?.let { list.add(it) }
                }
                data.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                println("DatabaseError for Popular: ${error.message}")
            }
        })
        return data
    }

    fun loadItemCategory(categoryId: String): LiveData<MutableList<ItemModel>> {
        val itemsLiveData = MutableLiveData<MutableList<ItemModel>>()
        val query: Query = db.child("Items").orderByChild("categoryId").equalTo(categoryId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (c in snapshot.children) {
                    c.getValue(ItemModel::class.java)?.let { list.add(it) }
                }
                itemsLiveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                println("DatabaseError for ItemCategory ($categoryId): ${error.message}")
                itemsLiveData.value = mutableListOf()
            }
        })
        return itemsLiveData
    }
}
