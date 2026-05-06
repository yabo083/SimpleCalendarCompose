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
│   └── di/                       ← 全局基础注入模块 (NetworkModule 只提供 OkHttp, DatabaseModule)
│
├── feature/                      ← 【业务功能层】(有独立页面，对应底部 Tab)
│   ├── calendar/                 ← 日历功能 (首页：月网格 + HorizontalPager 滑动 + 一言展示)
│   │   ├── di/                   ← 专属 DI 模块
│   │   ├── domain/               ← 领域模型 (CalendarDay)、用例 (GetMonthDaysUseCase)
│   │   └── ui/                   ← Screen、Grid、ViewModel、UiState (Map<LocalDate, List<CalendarDay>>)、导航扩展
│   │                                  Pager 驱动 ViewModel (snapshotFlow)，箭头直接驱动 Pager
│   │
│   ├── settings/                 ← 设置功能 (刷新间隔、壁纸来源)
│   │   ├── di/                   ← 专属 DI 模块
│   │   ├── domain/               ← 仓库接口 (SettingsRepository)
│   │   └── data/                 ← SharedPreferences 仓库实现
│   │
│   └── todo/                     ← 待办功能 (占位)
│       └── ui/                   ← TodoScreen
│
├── service/                      ← 【数据服务层】(无独立页面，为 feature 提供数据)
│   ├── hitokoto/                 ← 一言数据服务 (Hitokoto Retrofit API + LoadQuoteUseCase)
│   │   ├── di/                   ← 服务 DI 模块 (HitokotoModule)
│   │   ├── domain/               ← LoadQuoteUseCase
│   │   └── data/                 ← HitokotoApi、HitokotoResponse
│   │
│   ├── weather/                  ← 天气数据 (API 获取、Room 本地缓存)
│   │   ├── di/                   ← 服务 DI 模块 (WeatherModule)
│   │   ├── domain/               ← 仓库接口、用例 (RefreshWeatherUseCase)
│   │   └── data/                 ← Room 实体/DAO、Retrofit API、仓库实现、定位
│   │
│   └── wallpaper/                ← 壁纸缓存 (预拉取、每日轮换、偏好记录)
│       ├── di/                   ← 服务 DI 模块 (WallpaperModule)
│       ├── domain/               ← repository / usecase / model / policy (轮换、按宽度选图、缓存、偏好逻辑)
│       └── data/                 ← local(Room 实体/DAO)、repository(仓库实现)
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

### 三层结构小结

| 层 | 包含 | 有无 UI | 有无独立页面 |
|------|------|---------|------------|
| `feature/` | calendar, settings, todo | ✅ 有 ui/ 包 | ✅ 对应底部 Tab |
| `service/` | hitokoto, weather, wallpaper | ❌ 无 ui/ 包 | ❌ 无页面，数据注入 feature |
| `core/` | database, designsystem, di | ❌ | ❌ 纯基础设施 |

### 壁纸缓存关系
`daily_assignments` 使用普通 `image_id` 字段和索引记录每日分配，不再声明 Room 外键或级联删除。过期清理流程先删除引用过期图片的分配记录，再删除图片元数据和本地文件，避免级联行为扩大影响范围。壁纸 data 层按 `local / repository` 拆分，后续如果接入后端源，应新增 repository 实现，而不是改 UI。

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
├── networkModule (core.di)      — OkHttp
├── databaseModule (core.di)     — Room AppDatabase
├── hitokotoModule (service)     — HitokotoApi、LoadQuoteUseCase
├── weatherModule  (service)     — WeatherDao、WeatherRepository、LocationClient、RefreshWeatherUseCase
├── wallpaperModule (service)    — WallpaperDao、WallpaperRepository、WallpaperUseCase
├── settingsModule (feature)     — SettingsRepository、SettingsViewModel
└── calendarModule (feature)     — GetMonthDaysUseCase、LoadQuoteUseCase(service.hitokoto)、CalendarViewModel
```
