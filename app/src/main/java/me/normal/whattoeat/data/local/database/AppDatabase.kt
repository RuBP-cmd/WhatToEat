package me.normal.whattoeat.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import me.normal.whattoeat.data.local.dao.FoodDao
import me.normal.whattoeat.data.local.dao.FoodTableDao
import me.normal.whattoeat.data.local.entry.Food
import me.normal.whattoeat.data.local.entry.FoodTable

@Database(entities = [Food::class, FoodTable::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun foodTableDao(): FoodTableDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) { // v1为单表，单表迁移至v2的第一个表格中，表名为“默认”
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `food_table` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `created_at` INTEGER NOT NULL
                    )"""
                )
                db.execSQL(
                    "INSERT INTO `food_table` (`name`, `created_at`) VALUES ('默认', ${System.currentTimeMillis()})"
                )
                db.execSQL(
                    "ALTER TABLE `food` ADD COLUMN `table_id` INTEGER NOT NULL DEFAULT 1"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database_name"
                ).addMigrations(MIGRATION_1_2).build().also { INSTANCE = it }
                instance
            }
        }
    }
}
