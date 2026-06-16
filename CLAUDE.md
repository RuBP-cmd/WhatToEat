# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development

- **Build**: `./gradlew assembleDebug` (or use Android Studio — sync Gradle, then Run)
- **Lint**: `./gradlew lint`
- **Unit tests**: `./gradlew test`
- **Instrumented tests**: `./gradlew connectedAndroidTest`
- **Single test**: `./gradlew test --tests "me.normal.whattoeat.ExampleUnitTest"`
- **Clean rebuild**: `./gradlew clean assembleDebug`

## Architecture Overview

Single-Activity Android app (`MainActivity`) using Jetpack Compose + Material 3 with manual dependency injection via `MainApplication`.

### Dependency injection

`MainApplication` exposes two repositories as `by lazy` singletons:
- `foodRepository: FoodRepository` — backed by Room
- `settingsRepository: SettingsRepository` — backed by DataStore

ViewModels access them through `LocalContext.current.applicationContext as MainApplication`, then pass them via `viewModelFactory { initializer { ... } }`. There is **no DI framework** (no Hilt/Koin).

### Navigation

Type-safe Navigation Compose routes defined as `@Serializable object`s in `MainScreen.kt`: `Home`, `Settings`, `Eat`, `FoodEdit`, `PracticalWebsite`, `Other`. The bottom nav bar only appears on `Home` and `Settings`; other screens use `AppTopBar` with a back button. Navigation callbacks are passed as lambdas from `MainScreen` down to child composables.

### Data layer

- **Room**: `AppDatabase` (singleton via double-checked locking) → `FoodDao` → `FoodRepository`. Entity: `Food` — `id`, `timeStamp`, `name`, `weight`, `marked` (boolean for "included in random selection").
- **DataStore**: `Settings.kt` (`Context.dataStore` extension property, file name `"settings"`) → `SettingsRepository`. Currently stores only the color theme preference as a string key `"color_theme"`.

### UI layer

**Screens** (`ui/screens/`):
- `HomeScreen` — entry point with card buttons to Eat, Practical Websites, speed test (external URL), and Other
- `EatScreen` — weighted random food picker; actions: query, clear, ignore (marks `marked=false`), restore all
- `FoodEditScreen` — editable table (LazyColumn with sticky header) to CRUD food items
- `SettingsScreen` — color theme picker (5 options) + app info (version + GitHub link)
- `PracticalWebsiteScreen` — list of hardcoded utility website links (university services, billing, etc.)
- `OtherScreen` — an easter egg page with a click counter that reveals an image after 25 clicks

**Reusable components** (`ui/components/`):
- `CardButton` — primary clickable surface (two overloads: simple text+icon, and title+subtitle+icon+chevron)
- `AppTopBar` — top bar with back arrow, optional title, optional overflow menu
- `TitleCard` — bordered card with a colored title header
- `RowItem` — weighted horizontal row of `Cell` composables (used in the edit table)
- `BoxText` — centered text in a colored box
- `ElegantButton` — gradient-filled shadowed button (used only in OtherScreen)
- `AppIconButton` — wrapper around `IconButton` with 48dp min touch target

**Theme** (`ui/theme/`): 5 `ColorTheme` enum values (Blue, Yellow, Green, Pink, Purple), each mapping to a `lightColorScheme`. Only Purple has a `darkColorScheme`. Theme is persisted via DataStore; `MainActivity` reads it from `colorThemeFlow` and passes it to `WhatToEatTheme`.

### Key patterns

- Screens often split into an outer composable (handles ViewModel/state) and an inner `*Content` composable (pure UI with lambda callbacks) for testability and preview support.
- `FoodViewModel` manages a `StateFlow<List<Food>>` (converted from Room's `Flow` via `.stateIn()`) and performs weighted random selection with exclusion of the last chosen item.
- The `Cell` model class (`model/model.kt`) wraps a `@Composable` lambda with a `weight: Float` for proportional row layout — used only by `RowItem` in the food edit table.
