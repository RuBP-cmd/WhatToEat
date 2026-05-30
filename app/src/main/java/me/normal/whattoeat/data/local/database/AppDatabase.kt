package me.normal.whattoeat.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.normal.whattoeat.data.local.dao.FoodDao
import me.normal.whattoeat.data.local.entry.Food

@Database(entities = [Food::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun foodDao(): FoodDao

    companion object {
        // 使用 volatile 确保变量可见性
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database_name"
                ).build().also { INSTANCE = it }
                instance
            }
        }
    }
}