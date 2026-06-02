package me.normal.whattoeat.data.local.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.normal.whattoeat.ui.theme.ColorTheme

val Context.dataStore by preferencesDataStore("settings")


object Settings{

}
