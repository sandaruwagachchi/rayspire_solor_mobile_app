package com.example.pizzazone.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pizzazone.Domain.CategoryModel
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.Repository.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        return repository.loadCategory()
    }

    fun loadPopular(): LiveData<MutableList<ItemModel>> {
        return repository.loadPopular()
    }

    fun loadItems(categoryId: String): LiveData<MutableList<ItemModel>> {
        return repository.loadItemCategory(categoryId)
    }

    fun deleteItem(itemId: String): LiveData<Boolean> {
        return repository.deleteItem(itemId)
    }

    // New function to update an item
    fun updateItem(item: ItemModel): LiveData<Boolean> {
        return repository.updateItem(item)
    }
}