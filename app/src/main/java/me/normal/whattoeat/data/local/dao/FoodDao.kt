package me.normal.whattoeat.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.normal.whattoeat.data.local.entry.Food

@Dao
interface FoodDao {
    @Query("SELECT * FROM food ORDER BY time_stamp ASC")
    fun getAll(): Flow<List<Food>>

    @Update
    fun update(food: Food)
    @Insert
    fun insert(food: Food)

    @Delete
    fun delete(food: Food)
}