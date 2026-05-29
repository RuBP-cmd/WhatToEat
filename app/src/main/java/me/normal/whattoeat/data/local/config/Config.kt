package me.normal.whattoeat.data.local.config

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.normal.whattoeat.ui.theme.ColorTheme

val Context.dataStore by preferencesDataStore("config")


object Config{

    private val COLOR_THEME = stringPreferencesKey("color_theme")// 把字符串封装成对象，可以防止拼写错误

    fun colorThemeFlow(context: Context): Flow<ColorTheme> {
        return context.dataStore.data.map{ preferences -> // 将preferences的流转化为ColorTheme的流
            var str = preferences[COLOR_THEME] ?: "Pink" // 先提取处字符串

            ColorTheme.entries.find{ it.name == str } ?: ColorTheme.Pink // 再枚举类型中找有没有这个，没有就返回默认的
        }
    }

    suspend fun saveColorTheme(
        context: Context,
        theme: ColorTheme
    ) {
        context.dataStore.edit {
            it[COLOR_THEME] = theme.name
        }
    }
}
