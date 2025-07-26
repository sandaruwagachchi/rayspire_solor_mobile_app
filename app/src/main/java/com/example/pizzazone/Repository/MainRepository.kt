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
                data.value = mutableListOf()
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
                    val item = c.getValue(ItemModel::class.java)
                    item?.let {
                        it.id = c.key ?: ""
                        list.add(it)
                    }
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
                    val item = c.getValue(ItemModel::class.java)
                    item?.let {
                        it.id = c.key ?: ""
                        list.add(it)
                    }
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


    fun loadAllItems(): LiveData<MutableList<ItemModel>> {
        val allItemsLiveData = MutableLiveData<MutableList<ItemModel>>()
        db.child("Items").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (c in snapshot.children) {
                    val item = c.getValue(ItemModel::class.java)
                    item?.let {
                        it.id = c.key ?: ""
                        list.add(it)
                    }
                }
                allItemsLiveData.value = list
                Log.d("FIREBASE_ALL_ITEMS", "Loaded ${list.size} all items")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_ALL_ITEMS", "Error loading all items", error.toException())
                allItemsLiveData.value = mutableListOf()
            }
        })
        return allItemsLiveData
    }

    fun deleteItem(itemId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        db.child("Items").child(itemId).removeValue()
            .addOnSuccessListener {
                Log.d("FIREBASE_DELETE", "Item $itemId deleted successfully.")
                result.value = true
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_DELETE", "Failed to delete item $itemId", e)
                result.value = false
            }
        return result
    }


    fun updateItem(item: ItemModel): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        if (item.id.isNullOrEmpty()) {
            Log.e("FIREBASE_UPDATE", "Cannot update item: ID is null or empty.")
            result.value = false
            return result
        }


        val itemMap = mapOf(
            "title" to item.title,
            "description" to item.description,
            "picUrl" to item.picUrl,
            "price" to item.price,
            "rating" to item.rating,
            "numberInCart" to item.numberInCart,
            "categoryId" to item.categoryId
        )

        db.child("Items").child(item.id!!).updateChildren(itemMap)
            .addOnSuccessListener {
                Log.d("FIREBASE_UPDATE", "Item ${item.id} updated successfully.")
                result.value = true
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_UPDATE", "Failed to update item ${item.id}", e)
                result.value = false
            }
        return result
    }
}