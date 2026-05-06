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
│   └── di/                       ← Global DI modules (NetworkModule only provides OkHttp, DatabaseModule)
│
├── feature/                      ← Business feature layer (has own pages, tied to Tab navigation)
│   ├── calendar/                 ← Calendar feature (home page — month grid, date selection, quote display, horizontal swipe)
│   │   ├── di/                   ← Feature-specific DI (CalendarModule)
│   │   ├── domain/               ← Domain models (CalendarDay), use cases (GetMonthDaysUseCase)
│   │   └── ui/                   ← Screen, Grid, ViewModel, UiState (Map<LocalDate, List<CalendarDay>>), Navigation extension
│   │                                  Pager drives ViewModel via snapshotFlow (no reverse sync)
│   │
│   ├── settings/                 ← Settings feature (refresh intervals, wallpaper source)
│   │   ├── di/                   ← Feature-specific DI (SettingsModule)
│   │   ├── domain/               ← Repository interface (SettingsRepository)
│   │   └── data/                 ← SharedPreferences-based repository impl
│   │
│   └── todo/                     ← Todo feature (placeholder)
│       └── ui/                   ← TodoScreen
│
├── service/                      ← Data service layer (no pages, provide data to features)
│   ├── hitokoto/                 ← Quote data service (Hitokoto Retrofit API + LoadQuoteUseCase)
│   │   ├── di/                   ← Service DI (HitokotoModule)
│   │   ├── domain/               ← LoadQuoteUseCase
│   │   └── data/                 ← HitokotoApi, HitokotoResponse
│   │
│   ├── weather/                  ← Weather data (API fetch, local cache)
│   │   ├── di/                   ← Service DI (WeatherModule)
│   │   ├── domain/               ← Repository interfaces, use cases (RefreshWeatherUseCase)
│   │   └── data/                 ← Room entities, DAOs, Retrofit API, repository impl, location
│   │
│   └── wallpaper/                ← Wallpaper cache (pre-fetch, daily rotation, preferences)
│       ├── di/                   ← Service DI (WallpaperModule)
│       ├── domain/               ← repository / usecase / model (daily rotation, width-aware pre-cache logic)
│       └── data/                 ← local Room entities/DAO + repository implementations
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
Shared across all layers. Contains:
- Room database definition (`core/database/`)
- Design system / theme (`core/designsystem/`)
- Global DI modules for infrastructure (`core/di/`)

### Service Layer
Data services with no independent UI. Used by features via repository interfaces. Follows the same clean architecture as features but without `ui/`:
- `service/weather/` — weather data pipeline (Retrofit → Room → Repository). Owns `WeatherApi` creation in `WeatherModule`.
- `service/hitokoto/` — quote fetch pipeline (Retrofit API → LoadQuoteUseCase). Owns `HitokotoApi` creation in `HitokotoModule`.
- `service/wallpaper/` — wallpaper pre-cache, daily rotation, preference tracking. `domain/model` holds pure request models, `domain/policy` holds deterministic selection rules, `data/local` owns Room entities/DAO, and `data/repository` owns concrete repository implementations. Daily assignments intentionally store `image_id` without a Room foreign key/cascade; cleanup removes expired assignment rows explicitly before deleting image rows.

### Feature Layer
Business features with own pages and Tab navigation:
- Each feature is self-contained with `domain/data/ui/di`
- Cross-feature references are minimized
- Features consume services through injected repository interfaces

### App Layer
Assembly point. Wires features and services together via:
- `CalendarApplication` — Koin module registration (both `feature/*` and `service/*` modules)
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

## Visual Documentation Conventions

Data flow and control flow diagrams for this project follow a custom standard defined in [`docs/diagram-conventions.md`](docs/diagram-conventions.md). Key highlights:

- **Data Flow Diagram**: IN → Processing (key code) → OUT three-element format, with Chinese annotations. Left-to-right pipeline: API → Repository → Room DB → ViewModel → UI.
- **Control Flow Diagram**: Mermaid-based flowcharts with colored nodes for init paths, user interactions, background loops, and error/fallback branches.
- **Tool**: Whimsical (via MCP tools), with explicit coordinate positioning for data flow and auto-layout via `flowchart_create` for control flow.

## External Documentation

| Document | Purpose |
|----------|---------|
| [`docs/dg.md`](docs/dg.md) | Current concise coding guide. Prefer this for future development decisions. |
| [`docs/通用分层架构说明.md`](docs/%E9%80%9A%E7%94%A8%E5%88%86%E5%B1%82%E6%9E%B6%E6%9E%84%E8%AF%B4%E6%98%8E.md) | General directory placement guide from data sources to UI. |
| [`docs/Backend Design.md`](docs/Backend%20Design.md) | Wallpaper proxy backend service — optional companion project. Multi-source API aggregation, ring-pool with archive, preference aggregation. Not required for Phase 4 client. |

## Module Dependencies (Koin)
```
CalendarApplication
├── networkModule (core.di)      — OkHttp
├── databaseModule (core.di)     — Room AppDatabase
├── hitokotoModule (service)     — HitokotoApi, LoadQuoteUseCase
├── weatherModule  (service)     — WeatherDao, WeatherRepository, LocationClient, RefreshWeatherUseCase
├── wallpaperModule (service)    — WallpaperDao, WallpaperRepository, WallpaperUseCase
├── settingsModule (feature)     — SettingsRepository, SettingsViewModel
└── calendarModule (feature)     — GetMonthDaysUseCase, LoadQuoteUseCase (service.hitokoto), CalendarViewModel
```
