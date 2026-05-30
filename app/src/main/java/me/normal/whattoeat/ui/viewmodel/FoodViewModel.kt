package me.normal.whattoeat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.normal.whattoeat.data.local.entry.Food
import me.normal.whattoeat.data.repository.FoodRepository

class FoodViewModel (
    private val repository: FoodRepository
): ViewModel() {
    val foods: StateFlow<List<Food>> = repository.getAll()
        .stateIn( // Flow变为StateFlow
            scope = viewModelScope, // 绑定ViewModel的生命周期
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insert(food: Food){
        viewModelScope.launch{
            repository.insert(food)
        }
    }

    fun update(food: Food){
        viewModelScope.launch{
            repository.update(food)
        }
    }

    fun delete(food: Food){
        viewModelScope.launch {
            repository.delete(food)
        }
    }

    fun choosenRandomFood(): String{
        val foodList = foods.value.filter{ food -> food.marked }

        if(foodList.isEmpty()) return "列表为空！"

        var totalWeight = 0
        for(food in foodList){
            totalWeight += food.weight
        }


        var random = Math.random()
        var sumWeight = 0

        for(food in foodList){
            sumWeight += food.weight
            if(sumWeight.toDouble() / totalWeight >= random) return food.name
        }
        return foodList.last().name
    }
}