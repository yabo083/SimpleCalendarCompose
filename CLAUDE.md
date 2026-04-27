# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

- Build (debug): `./gradlew assembleDebug`
- Run tests: `./gradlew test` (unit), `./gradlew connectedAndroidTest` (instrumented)
- Run single unit test: `./gradlew app:test --tests "com.example.calendar.*"`
- Lint: `./gradlew lint`
- Clean: `./gradlew clean`

## Project Architecture

Single-module Android app (Jetpack Compose, Material3) with **feature-first** package structure and clean architecture layers inside each feature.

### Package Structure

```
com.example.calendar/
  app/                        -- Assembly: Application class, MainActivity, Navigation graph
  core/
    database/                 -- Room AppDatabase definition
    designsystem/             -- Theme, colors, typography, shared composables
    di/                       -- Global DI modules (NetworkModule, DatabaseModule)
  feature/
    calendar/                 -- Home feature: month grid, date selection, hitokoto quotes
    weather/                  -- Weather API fetch + Room local cache
    settings/                 -- Refresh interval configuration
    todo/                     -- Placeholder screen
```

### Within a Feature

| Layer | Responsibility |
|-------|---------------|
| `domain/` | Use cases, repository interfaces, pure Kotlin models |
| `data/` | Repository impls, Room DAOs/entities, Retrofit APIs, mappers |
| `ui/` | Compose screens, ViewModels, UI state, Navigation extensions |
| `di/` | Koin module for feature wiring |

### Key Patterns

- **DI**: Koin modules registered in `CalendarApplication`. Feature modules depend on `core/` infrastructure modules.
- **Navigation**: `AppNavHost` composites feature navigation graphs. Each feature exposes an extension function on `NavGraphBuilder`. Bottom nav defined in `AppDestination` enum.
- **State**: ViewModels expose `StateFlow<UiState>`. Calendar uses `combine` on multiple flows (_currentMonth, _selectedDate, weather, quote) into a single UiState.
- **Calendar Pager**: `HorizontalPager` drives month changes. A `snapshotFlow` on the current page index calls `onMonthChanged` on the ViewModel — there is no reverse state sync.
- **Weather**: Periodic refresh loop inside `CalendarViewModel` using a configurable `settingsRepository` interval (default 30 min). Retrofit + Room for network + local cache.
- **API Keys**: Weather API key read from `local.properties` as `weather.api.key=...`, exposed via `BuildConfig.WEATHER_API_KEY`.

### Dependencies

- Kotlin 2.1.0, AGP 8.7.2, Compose BOM 2024.12.01, Room 2.8.4, Retrofit 3.0.0, Koin 4.0.2, Navigation Compose 2.8.5
- Min SDK 26, Target SDK 35, Java 21
