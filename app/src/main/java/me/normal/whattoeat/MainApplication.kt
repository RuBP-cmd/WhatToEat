package me.normal.whattoeat

import android.app.Application
import me.normal.whattoeat.data.local.database.AppDatabase
import me.normal.whattoeat.data.local.settings.dataStore
import me.normal.whattoeat.data.repository.FoodRepository
import me.normal.whattoeat.data.repository.SettingsRepository

class MainApplication : Application() {

    val foodRepository by lazy {
        FoodRepository(AppDatabase.getInstance(this).foodDao())
    }
    val settingsRepository by lazy {
        SettingsRepository(this.dataStore)
    }

    override fun onCreate() {
        super.onCreate()

    }
}