// package com.example.pizzazone.ViewModel
// MainViewModel.kt
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

    // HERE IS THE CHANGE: Change parameter type to String
    fun loadItems(categoryId: String):LiveData<MutableList<ItemModel>>{
        return repository.loadItemCategory(categoryId)
    }
}