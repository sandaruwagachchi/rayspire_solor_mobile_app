package com.example.pizzazone.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.Repository.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    fun loadCategory() = repository.loadCategory()

    fun loadPopular(): LiveData<List<ItemModel>> {
        return repository.loadPopular()
    }

    fun loadItems(categoryId:String):LiveData<MutableList<ItemModel>>{
        return repository.loadItemCategory(categoryId)
    }
}
