package me.normal.whattoeat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.normal.whattoeat.data.local.entry.Food
import me.normal.whattoeat.data.local.entry.FoodTable
import me.normal.whattoeat.data.repository.FoodRepository
import me.normal.whattoeat.data.repository.FoodTableRepository

@OptIn(ExperimentalCoroutinesApi::class)
class FoodViewModel(
    private val foodRepository: FoodRepository,
    private val foodTableRepository: FoodTableRepository
) : ViewModel() {

    // 所有表格
    val tables: StateFlow<List<FoodTable>> = foodTableRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 当前选中的表格 id
    private val _currentTableId = MutableStateFlow(1)
    val currentTableId: StateFlow<Int> = _currentTableId.asStateFlow()

    // 当前表格中的食物，切换表格时自动更新
    val foods: StateFlow<List<Food>> = _currentTableId.flatMapLatest { tableId ->
        foodRepository.getByTableId(tableId)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var chosenFood = Food(name = "", weight = 1, marked = true, tableId = 1)

    init {
        // 首次启动无表时自动创建"默认"表格
        viewModelScope.launch {
            val existing = foodTableRepository.getAll().first()
            if (existing.isEmpty()) {
                foodTableRepository.insert(FoodTable(name = "默认"))
            }
        }
    }

    // --- 表格管理 ---

    fun switchTable(tableId: Int) {
        if (tableId == _currentTableId.value) return
        _currentTableId.value = tableId
        chosenFood = Food(name = "", weight = 1, marked = true, tableId = tableId)
    }

    fun createTable(name: String) {
        viewModelScope.launch {
            foodTableRepository.insert(FoodTable(name = name))
        }
    }

    fun renameTable(tableId: Int, newName: String) {
        viewModelScope.launch {
            val target = tables.value.find { it.id == tableId } ?: return@launch
            foodTableRepository.update(target.copy(name = newName))
        }
    }

    fun deleteTable(tableId: Int) {
        viewModelScope.launch {
            foodRepository.deleteByTableId(tableId)
            foodTableRepository.deleteById(tableId)
            if (_currentTableId.value == tableId) {
                // 切换到剩余的表中
                val remaining = tables.value.filter { it.id != tableId }
                if (remaining.isNotEmpty()) {
                    _currentTableId.value = remaining.first().id
                }
            }
        }
    }

    // --- 食物 CRUD ---

    fun insert(food: Food) {
        viewModelScope.launch {
            foodRepository.insert(food.copy(tableId = _currentTableId.value))
        }
    }

    fun update(food: Food) {
        viewModelScope.launch {
            foodRepository.update(food)
        }
    }

    fun delete(food: Food) {
        viewModelScope.launch {
            foodRepository.delete(food)
        }
    }

    // --- 随机选择 ---

    fun chosenRandomFood(): String {
        val foodList = foods.value.filter { food ->
            food.marked && food.weight > 0 && food.name.isNotEmpty()
        }

        if (foodList.isEmpty()) return "列表为空！"

        val candidates = if (foodList.size == 1) {
            foodList
        } else {
            foodList.filter { food -> food != chosenFood }
        }

        val totalWeight = candidates.sumOf { food -> food.weight }
        val random = Math.random()
        var sumWeight = 0

        for (food in candidates) {
            sumWeight += food.weight
            if (sumWeight.toDouble() / totalWeight >= random) {
                chosenFood = food
                return chosenFood.name
            }
        }
        chosenFood = candidates.last()
        return chosenFood.name
    }

    fun ignoreChosenFood() {
        update(chosenFood.copy(marked = false))
    }

    fun clearAllIgnore() {
        viewModelScope.launch {
            foodRepository.updateAllMarked(_currentTableId.value, marked = true)
        }
    }
}
