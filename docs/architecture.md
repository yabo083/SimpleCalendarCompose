# SimpleCalendarCompose 架构文档

## 架构总览

项目采用 **Feature-First 包结构**，以业务功能为核心组织代码，每个 Feature 内部遵循 Clean Architecture 分层。

```
com.example.calendar/
├── app/                          ← 【全局组装厂】
│   ├── CalendarApplication.kt    ← Koin + Timber 启动
│   ├── CalendarApp.kt            ← 根 Composable (Scaffold + BottomNav)
│   ├── MainActivity.kt           ← 单 Activity 宿主
│   └── navigation/               ← 全局路由定义 (AppDestination, AppNavHost)
│
├── core/                         ← 【基础设施层】(多 Feature 共享)
│   ├── database/                 ← Room Database 定义 (AppDatabase)
│   ├── designsystem/             ← 全局主题、颜色、字体、通用组件
│   └── di/                       ← 全局基础注入模块 (NetworkModule, DatabaseModule)
│
├── feature/                      ← 【业务功能层】
│   ├── calendar/                 ← 日历功能 (首页：月网格 + HorizontalPager 滑动 + 一言语录)
│   │   ├── di/                   ← 专属 DI 模块
│   │   ├── domain/               ← 领域模型 (CalendarDay)、用例 (GetMonthDaysUseCase, LoadQuoteUseCase)
│   │   ├── data/                 ← HitokotoApi、HitokotoResponse (一言语录 Retrofit)
│   │   └── ui/                   ← Screen、Grid、ViewModel、UiState (Map<LocalDate, List<CalendarDay>>)、导航扩展
│   │                                  Pager 驱动 ViewModel (snapshotFlow)，箭头直接驱动 Pager
│   │
│   ├── weather/                  ← 天气功能 (API 获取、本地缓存)
│   │   ├── di/                   ← 专属 DI 模块
│   │   ├── domain/               ← 仓库接口、用例 (RefreshWeatherUseCase)
│   │   └── data/                 ← Room 实体/DAO、Retrofit API、仓库实现、定位
│   │
│   ├── settings/                 ← 设置功能 (刷新间隔)
│   │   ├── di/                   ← 专属 DI 模块
│   │   ├── domain/               ← 仓库接口 (SettingsRepository)
│   │   └── data/                 ← SharedPreferences 仓库实现
│   │
│   └── todo/                     ← 待办功能 (占位)
│       └── ui/                   ← TodoScreen
```

## 架构原则

### Feature-First 组织
每个 Feature 自包含于独立包中，拥有完整的 domain/data/ui/DI 各层。跨 Feature 的引用尽量减少，必要时可直接引用（同一模块内）。

### 各层职责
| 层 | 职责 |
|-------|---------------|
| `domain/` | 业务逻辑、仓库接口、纯 Kotlin 数据模型 |
| `data/` | 仓库实现、Room DAO/Entity、Retrofit API、数据映射 |
| `ui/` | Compose 界面、ViewModel、UI 状态类 |
| `di/` | Koin 模块定义（Feature 专属依赖注入） |

### 依赖方向
```
UI (ViewModel) → Domain (UseCase → Repository 接口)
                                     ↓
                              Data (RepositoryImpl → DAO/API)
```

### 技术栈
- **语言**: Kotlin 2.1.0
- **UI**: Jetpack Compose (BOM 2024.12.01) + Material3
- **数据库**: Room 2.8.4 (KSP)
- **网络**: Retrofit 3.0.0 + OkHttp 4.12.0
- **DI**: Koin 4.0.2
- **导航**: Navigation Compose 2.8.5
- **异步**: Kotlin Coroutines + Flow
- **日志**: Timber 5.0.1
- **Min SDK**: 26 | **Target SDK**: 35

### DI 模块注册
```
CalendarApplication
├── networkModule (core.di)     — OkHttp、Retrofit、WeatherApi
├── databaseModule (core.di)    — Room AppDatabase
├── weatherModule  (feature)    — WeatherDao、WeatherRepository、LocationClient、RefreshWeatherUseCase
├── settingsModule (feature)    — SettingsRepository
└── calendarModule (feature)    — GetMonthDaysUseCase、LoadQuoteUseCase、CalendarViewModel
```
