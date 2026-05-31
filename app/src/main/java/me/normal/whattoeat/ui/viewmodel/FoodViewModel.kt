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
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    var chosenFood = Food(name = "", weight = 1, marked = true)

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
        val foodList = foods.value.filter{ food -> food.marked && !food.name.isEmpty() }

        if(foodList.isEmpty()) return "列表为空！"

        if(foodList.size == 1) {
            chosenFood = foodList.first()
            return chosenFood.name
        }

        var totalWeight = 0
        for(food in foodList){
            if(food == chosenFood) continue // 排除上一次的选择
            totalWeight += food.weight
        }

        val random = Math.random()
        var sumWeight = 0

        for(food in foodList){
            sumWeight += food.weight
            if(sumWeight.toDouble() / totalWeight >= random && food != chosenFood) {
                chosenFood = food
                return chosenFood.name
            }
        }
        chosenFood = foodList.last()
        return chosenFood.name
    }

    fun ignoreChosenFood(){
        update(chosenFood.copy(marked = false))
    }

    fun clearAllIgnore(){
        for(food in foods.value){
            if(!food.marked){
                update(food.copy(marked = true))
            }
        }
    }
}