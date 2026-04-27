# Project Architecture

## Overview

`SimpleCalendarCompose` is a single-module Android application built with Jetpack Compose, following a **feature-first package structure** with clean architecture layers inside each feature.

## Package Structure

```
com.example.calendar/
├── app/                          ← Global assembly factory
│   ├── CalendarApplication.kt    ← Koin + Timber initialization
│   ├── CalendarApp.kt            ← Root composable (Scaffold + BottomNav)
│   ├── MainActivity.kt            ← Single Activity host
│   └── navigation/               ← Global route definitions (AppDestination, AppNavHost)
│
├── core/                         ← Infrastructure layer (shared across features)
│   ├── database/                 ← Room Database definition (AppDatabase)
│   ├── designsystem/             ← Global theme, colors, typography, shared composables
│   └── di/                       ← Global DI modules (NetworkModule, DatabaseModule)
│
├── feature/                      ← Business feature layer
│   ├── calendar/                 ← Calendar feature (home page — month grid, date selection, quotes, horizontal swipe)
│   │   ├── di/                   ← Feature-specific DI (CalendarModule)
│   │   ├── domain/               ← Domain models (CalendarDay), use cases (GetMonthDaysUseCase, LoadQuoteUseCase)
│   │   ├── data/                 ← HitokotoApi, HitokotoResponse (Retrofit for quotes)
│   │   └── ui/                   ← Screen, Grid, ViewModel, UiState (Map<LocalDate, List<CalendarDay>>), Navigation extension
│   │                                  Pager drives ViewModel via snapshotFlow (no reverse sync)
│   │
│   ├── weather/                  ← Weather feature (API fetch, local cache)
│   │   ├── di/                   ← Feature-specific DI (WeatherModule)
│   │   ├── domain/               ← Repository interfaces, use cases (RefreshWeatherUseCase)
│   │   └── data/                 ← Room entities, DAOs, Retrofit API, repository impl, location
│   │
│   ├── settings/                 ← Settings feature (refresh intervals)
│   │   ├── di/                   ← Feature-specific DI (SettingsModule)
│   │   ├── domain/               ← Repository interface (SettingsRepository)
│   │   └── data/                 ← SharedPreferences-based repository impl
│   │
│   └── todo/                     ← Todo feature (placeholder)
│       └── ui/                   ← TodoScreen
```

## Architecture Principles

### Feature-First Organization
Each feature is self-contained within its own package, containing all layers (domain, data, UI, DI) needed for that feature. Cross-feature references are minimized.

### Layer Responsibilities
| Layer | Responsibility |
|-------|---------------|
| `domain/` | Business logic, repository interfaces, pure Kotlin models |
| `data/` | Repository implementations, Room DAOs/entities, Retrofit APIs, mappers |
| `ui/` | Compose screens, ViewModels, UI state classes |
| `di/` | Koin module definitions for feature-specific wiring |

### Dependency Flow
```
UI (ViewModel) → Domain (UseCase → Repository interface)
                                     ↓
                              Data (RepositoryImpl → DAO/API)
```

### Core Layer
Shared across features. Contains:
- Room database definition (`core/database/`)
- Design system / theme (`core/designsystem/`)
- Global DI modules for infrastructure (`core/di/`)

### App Layer
Assembly point. Wires features together via:
- `CalendarApplication` — Koin module registration
- `AppNavHost` — Navigation graph composition
- `CalendarApp` — Bottom navigation + Scaffold

## Key Technology Stack
- **Language**: Kotlin 2.1.0
- **UI**: Jetpack Compose (BOM 2024.12.01) + Material3
- **Database**: Room 2.8.4 (with KSP)
- **Network**: Retrofit 3.0.0 + OkHttp 4.12.0
- **DI**: Koin 4.0.2
- **Navigation**: Navigation Compose 2.8.5
- **Async**: Kotlin Coroutines + Flow
- **Logging**: Timber 5.0.1
- **Min SDK**: 26 | **Target SDK**: 35

## Module Dependencies (Koin)
```
CalendarApplication
├── networkModule (core.di)     — OkHttp, Retrofit, WeatherApi
├── databaseModule (core.di)    — Room AppDatabase
├── weatherModule  (feature)    — WeatherDao, WeatherRepository, LocationClient, RefreshWeatherUseCase
├── settingsModule (feature)    — SettingsRepository
└── calendarModule (feature)    — GetMonthDaysUseCase, LoadQuoteUseCase, CalendarViewModel
```
