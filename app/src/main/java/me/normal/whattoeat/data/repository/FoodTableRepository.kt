package me.normal.whattoeat.data.repository

import kotlinx.coroutines.flow.Flow
import me.normal.whattoeat.data.local.dao.FoodTableDao
import me.normal.whattoeat.data.local.entry.FoodTable

class FoodTableRepository(
    private val dao: FoodTableDao
) {
    fun getAll(): Flow<List<FoodTable>> = dao.getAll()

    suspend fun insert(table: FoodTable): Long = dao.insert(table)

    suspend fun update(table: FoodTable) = dao.update(table)

    suspend fun delete(table: FoodTable) = dao.delete(table)

    suspend fun deleteById(tableId: Int) = dao.deleteById(tableId)
}
