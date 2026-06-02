package me.normal.whattoeat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.normal.whattoeat.data.repository.SettingsRepository
import me.normal.whattoeat.ui.theme.ColorTheme

class SettingsViewModel(
    private val repository: SettingsRepository
): ViewModel() {

    val colorTheme: StateFlow<ColorTheme> = repository.colorThemeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ColorTheme.Pink
    )

    fun saveColorTheme(colorTheme: ColorTheme){
        viewModelScope.launch{
            repository.saveColorTheme(colorTheme) // suspend的，需要在协程中
        }

    }


}