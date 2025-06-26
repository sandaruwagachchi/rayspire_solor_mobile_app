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
                for (c in snap.children) c.getValue(CategoryModel::class.java)?.let { list.add(it) }
                data.value = list
            }
            override fun onCancelled(err: DatabaseError) {}
        })
        return data
    }

    fun loadPopular(): LiveData<List<ItemModel>> {
        val data = MutableLiveData<List<ItemModel>>()
        db.child("Popular").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (c in snap.children) c.getValue(ItemModel::class.java)?.let { list.add(it) }
                data.value = list
            }
            override fun onCancelled(err: DatabaseError) {}
        })
        return data
    }

    fun loadItemCategory(categoryId:String):LiveData<MutableList<ItemModel>>{
        val itemsLiveData = MutableLiveData<MutableList<ItemModel>>()
        val ref = db.child("Items") // Corrected: Use 'db' here
        val query:Query=ref.orderByChild("categoryId").equalTo(categoryId)

        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) { // Renamed from 'snap' to 'snapshot' for consistency
                val list = mutableListOf<ItemModel>()
                for (c in snapshot.children) c.getValue(ItemModel::class.java)?.let { list.add(it) } // Corrected: Use snapshot.children
                itemsLiveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error appropriately, e.g., log it or set an error state
                // For now, it's just a TODO, but in a real app, you'd want to handle it.
            }
        })
        return itemsLiveData
    }

}