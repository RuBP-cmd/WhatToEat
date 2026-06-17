package me.normal.whattoeat.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import me.normal.whattoeat.ui.theme.ColorTheme

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys{
        val COLOR_THEME = stringPreferencesKey("color_theme") // 代码使用的键名 <- DataStore中的键名
        val CURRENT_TABLE_ID = intPreferencesKey("current_table_id") //
    }

    // dataStore.data为一个Flow<Preferences>，需要使用.map方法将Preferences->T，然后就能得到Flow<T>
    val colorThemeFlow = dataStore.data.map { preferences -> // 将preferences的流转化为ColorTheme的流
        var str = preferences[PreferencesKeys.COLOR_THEME] ?: "Pink" // 先提取处字符串

        ColorTheme.entries.find { it.name == str } ?: ColorTheme.Pink // 再枚举类型中找有没有这个，没有就返回默认的
    }

    val currentTableIdFlow = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CURRENT_TABLE_ID] ?: 1   // 不存在返回1
    }


    suspend fun saveColorTheme(theme: ColorTheme) {
        dataStore.edit {
            it[PreferencesKeys.COLOR_THEME] = theme.name
        }
    }

    suspend fun saveCurrentTableId(currentTableId: Int){
        dataStore.edit {
            it[PreferencesKeys.CURRENT_TABLE_ID] = currentTableId
        }
    }
}