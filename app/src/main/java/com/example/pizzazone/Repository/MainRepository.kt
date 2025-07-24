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
        // Query "Items" directly, you might need to iterate through children to find matching categoryId
        // Firebase Realtime Database doesn't have direct indexing like Firestore without creating an index.
        // If your 'id' in ItemModel is what you use to uniquely identify and delete items,
        // and it matches the key under "Items", then direct child access is simplest.
        // Assuming 'id' in ItemModel is the key in Firebase's "Items" node
        val query: Query = db.child("Items").orderByChild("categoryId").equalTo(categoryId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (c in snapshot.children) {
                    // It's important to capture the actual Firebase key if that's your unique ID
                    val item = c.getValue(ItemModel::class.java)
                    item?.let {
                        // Firebase "id" field in ItemModel is based on the data, not necessarily the key.
                        // If the key is the actual ID, you should set it here.
                        // For example, if the database structure is:
                        // Items
                        //   -KJHDGKJHD878 (random push ID)
                        //     categoryId: "0"
                        //     description: "..."
                        //     id: "0" <--- This 'id' field
                        //     ...
                        // If 'id: "0"' is what you use for deletion, it needs to be unique in the database.
                        // If the random push ID is your actual unique ID, you'd do:
                        // it.id = c.key ?: it.id // Update item's ID with Firebase key if available

                        // Based on the screenshot, 'id: "0"' is a child of '0' under 'Items'.
                        // This means the "0" directly under "Items" is the unique key.
                        // So, if you're deleting by the 'id' field within ItemModel, ensure it corresponds to the Firebase key.
                        // If your ItemModel's 'id' field is intended to be the Firebase key for deletion,
                        // and it's unique across all items, then 'db.child("Items").child(itemId).removeValue()' works.
                        // Let's assume the 'id' field in ItemModel is the unique key used in Firebase for direct access.
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

    // New function to delete an item
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
}