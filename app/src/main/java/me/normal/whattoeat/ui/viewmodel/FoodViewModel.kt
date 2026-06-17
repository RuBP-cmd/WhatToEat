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
import me.normal.whattoeat.data.repository.SettingsRepository

@OptIn(ExperimentalCoroutinesApi::class)
class FoodViewModel(
    private val foodRepository: FoodRepository,
    private val foodTableRepository: FoodTableRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // 所有表格
    val tables: StateFlow<List<FoodTable>> = foodTableRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 当前选中的表格 id，将Flow转为StateFlow
    val currentTableId: StateFlow<Int> = settingsRepository.currentTableIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 1
    )

    // 当前表格中的食物，切换表格时自动更新
    val foods: StateFlow<List<Food>> = currentTableId.flatMapLatest { tableId ->
        foodRepository.getByTableId(tableId)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var chosenFood: Food? = null

    init {
        // 首次启动无表时自动创建"默认"表格
        viewModelScope.launch {
            if(foodTableRepository.getAll().first().isEmpty()){
                createTable("默认")
            }
        }
    }

    // --- 表格管理 ---

    fun switchTable(tableId: Int) {
        if (tableId == currentTableId.value) return
        updateCurrentTableId(tableId)
        chosenFood = null
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

    // 删除表，注意删除的表是正在使用的表的情况，需要将表id切换到剩余的表
    fun deleteTable(tableId: Int) {
        viewModelScope.launch {
            foodRepository.deleteByTableId(tableId)
            foodTableRepository.deleteById(tableId)
            if (currentTableId.value == tableId) {
                // 切换到剩余的表中
                val remaining = tables.value.filter { it.id != tableId }
                if (remaining.isNotEmpty()) {
                    updateCurrentTableId(remaining.first().id)
                }
            }
        }
    }

    // --- 食物 CRUD ---

    fun insert(food: Food) {
        viewModelScope.launch { // ui层不管之table_id，因此移到这里添加
            foodRepository.insert(food.copy(tableId = currentTableId.value))
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
                return chosenFood!!.name // 一定非null
            }
        }
        chosenFood = candidates.last()
        return chosenFood!!.name
    }

    fun ignoreChosenFood() {
        update(chosenFood!!.copy(marked = false))
    }

    fun clearAllIgnore() {
        viewModelScope.launch {
            foodRepository.updateAllMarked(currentTableId.value, marked = true)
        }
    }

    fun updateCurrentTableId(currentTableId: Int){
        viewModelScope.launch {
            settingsRepository.saveCurrentTableId(currentTableId)
        }
    }
}
