# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此仓库中工作时提供指导。

## 构建与开发

- **构建**: `./gradlew assembleDebug`（或使用 Android Studio — 同步 Gradle，然后点击 Run）
- **代码检查**: `./gradlew lint`
- **单元测试**: `./gradlew test`
- **仪器化测试**: `./gradlew connectedAndroidTest`
- **单个测试**: `./gradlew test --tests "me.normal.whattoeat.ExampleUnitTest"`
- **清理重建**: `./gradlew clean assembleDebug`

## 架构概览

单 Activity Android 应用（`MainActivity`），采用**分层架构**：UI 层（Jetpack Compose + Material 3）→ ViewModel 层 → Repository 层 → 数据源层（Room + DataStore）。手动依赖注入，无 DI 框架。

```
MainActivity (入口, 读取主题 Flow)
  └─ MainScreen (导航图 + Scaffold + 底部导航栏)
       ├─ HomeScreen
       ├─ EatScreen ─────────────────────────────┐
       ├─ FoodEditScreen ────────────────────────┤
       ├─ SettingsScreen (SettingsViewModel) ─────┤ 共享 FoodViewModel
       ├─ PracticalWebsiteScreen                  │
       └─ OtherScreen                             │
                                                  │
FoodViewModel ◄──────────────────────────────────┘
  ├─ FoodRepository ──► FoodDao ──► Room (food 表)
  ├─ FoodTableRepository ──► FoodTableDao ──► Room (food_table 表)
  └─ SettingsRepository ──► DataStore (settings)
```

### 分层职责

| 层 | 包路径 | 职责 |
|----|--------|------|
| UI | `ui/screens/`, `ui/components/` | Compose 组合函数，纯 UI 渲染 + 用户交互回调 |
| ViewModel | `ui/viewmodel/` | 持有 `StateFlow` 状态，暴露业务操作方法，在 `viewModelScope` 中启动协程 |
| Repository | `data/repository/` | 对 DAO/DataStore 的薄封装，将数据源 API 转换为业务友好的方法签名 |
| DAO | `data/local/dao/` | Room `@Dao` 接口，定义 SQL 查询，返回 `Flow<List<T>>`（可观察）或 `suspend` 函数（一次性写操作） |
| 实体 | `data/local/entry/` | Room `@Entity` 数据类，直接映射到数据库表和列 |
| 数据源 | `data/local/database/`, `data/local/settings/` | Room 数据库实例 + DataStore 偏好存储 |

### 数据流（响应式核心链路）

应用采用 **Room → Flow → Repository → ViewModel → StateFlow → Compose** 的响应式数据流：

```
[SQLite 数据库]
     │  Room 自动生成查询
     ▼
[Flow<List<Food>>]  ←  Room DAO 返回，数据变更时自动发射新值
     │  Repository 透传
     ▼
[Flow<List<Food>>]  ←  FoodRepository（无额外转换）
     │  ViewModel 中调用 .stateIn() 转为 StateFlow（热流，Eagerly 启动）
     ▼
[StateFlow<List<Food>>]  ←  FoodViewModel.foods
     │  Composable 中调用 .collectAsState()
     ▼
[State<List<Food>>]  ←  Compose 自动订阅，Flow 发射新值时触发重组
     │
     ▼
[UI 自动刷新]
```

**关键转换节点**:
- **`FoodViewModel.foods`**: 使用 `currentTableId.flatMapLatest { tableId -> foodRepository.getByTableId(tableId) }` — 当切换表格时，`flatMapLatest` 自动取消旧表的 Flow 订阅并切换到新表，无需手动刷新
- **`FoodViewModel.tables`**: `foodTableRepository.getAll().stateIn(...)` — 所有表格列表，表格增删时自动更新
- **`SettingsViewModel.colorTheme`**: `repository.colorThemeFlow.stateIn(...)` — DataStore 的 `Flow<ColorTheme>` 转 `StateFlow`

**写操作链路**（以插入食物为例）:
```
用户点击添加按钮
  → FoodEditScreen 回调
  → FoodViewModel.insert(food)           // 在 viewModelScope.launch 中
  → FoodRepository.insert(food)          // suspend 函数
  → FoodDao.insert(food)                // Room @Insert
  → SQLite INSERT
  → Room 检测到 food 表变更
  → FoodDao.getByTableId() 的 Flow 自动发射新列表
  → StateFlow 更新
  → Compose 重组，UI 显示新数据
```

## 依赖注入

`MainApplication` 通过 `by lazy` 单例暴露三个仓库：
- `foodRepository: FoodRepository` — 由 Room 支持
- `foodTableRepository: FoodTableRepository` — 由 Room 支持
- `settingsRepository: SettingsRepository` — 由 DataStore 支持

ViewModel 通过 `LocalContext.current.applicationContext as MainApplication` 访问 Application，再通过 `viewModelFactory { initializer { ... } }` 将仓库注入 ViewModel 构造函数。**没有使用 DI 框架**（无 Hilt/Koin）。

示例（`MainScreen.kt` 第 64-75 行）:
```kotlin
val application = LocalContext.current.applicationContext as MainApplication
val foodViewModel: FoodViewModel = viewModel(
    factory = viewModelFactory {
        initializer {
            FoodViewModel(
                application.foodRepository,
                application.foodTableRepository,
                application.settingsRepository
            )
        }
    }
)
```

## 数据层

### Room 数据库

`AppDatabase`（`data/local/database/`）：单例（双重检查锁定），版本 v2，包含两个 DAO：

**`FoodDao` → `FoodRepository`**
- 实体 `Food`：`id`、`timeStamp`、`name`、`weight`、`marked`（是否参与随机选择）、`tableId: Int`（外键，关联 `food_table.id`）
- 查询方法：
  - `getByTableId(tableId): Flow<List<Food>>` — 按表查询，响应式
  - `getAll(): Flow<List<Food>>` — 全部食物
  - `insert(food)` / `update(food)` / `delete(food)` — CRUD（suspend）
  - `deleteByTableId(tableId)` — 删除表中全部食物
  - `updateAllMarked(tableId, marked)` — 批量修改 marked 标记

**`FoodTableDao` → `FoodTableRepository`**
- 实体 `FoodTable`：`id`、`name`、`createdAt`
- 查询方法：
  - `getAll(): Flow<List<FoodTable>>` — 全部表格，按创建时间升序
  - `insert(table): Long` / `update(table)` / `deleteById(tableId)` — 表格管理

**数据库迁移**（`MIGRATION_1_2`）：v1（单表）→ v2（多表格），创建 `food_table` 表，插入"默认"表格，为 `food` 表添加 `table_id` 列。

### DataStore

`Settings.kt`：`Context.dataStore` 扩展属性（文件名 `"settings"`）。

**`SettingsRepository`** 管理两个偏好键：
| 键名 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `"color_theme"` | `String` | `"Pink"` | 存储 `ColorTheme` 枚举名 |
| `"current_table_id"` | `Int` | `1` | 当前选中的食物表格 id |

采用 `dataStore.data.map { ... }` 将 `Flow<Preferences>` 映射为 `Flow<ColorTheme>` / `Flow<Int>`，读操作响应式；写操作通过 `dataStore.edit { ... }`，为 `suspend` 函数。

## UI 层

### 页面 (`ui/screens/`)

- **`MainScreen`** — 顶层导航宿主：定义 6 个 `@Serializable object` 路由，创建 `FoodViewModel` 并注入依赖，通过 `NavHost` 切换页面。底部导航栏（`MainScreenNav`）仅在 `Home` 和 `Settings` 时显示
- **`HomeScreen`** — 入口页面，卡片按钮跳转到"吃什么"、"实用网站"、网速测试（外部 URL）和"其他"
- **`EatScreen`** — 加权随机食物选择器；操作按钮：随机选出食物、清空结果、忽略（将当前食物标记为 `marked=false`）、恢复全部标记；右侧 `BookmarkSidebar` 可展开/收起表名缩写，用于快速切换表格
- **`FoodEditScreen`** — 可编辑表格，顶部 `PrimaryScrollableTabRow` 切换表格（长按删除、双击重命名），`LeftSwipeBox` 包裹每行（左滑露出删除按钮），删除操作通过 `ConfirmDialog` 二次确认
- **`SettingsScreen`** — 颜色主题选择器（5 个选项）+ 应用信息（版本号 + GitHub 链接），使用独立的 `SettingsViewModel`
- **`PracticalWebsiteScreen`** — 硬编码的实用网站链接列表（`model/PracticalWebsites.kt`，8 个链接）
- **`OtherScreen`** — 彩蛋页面，点击计数器 25 次后显示图片

### 可复用组件 (`ui/components/`)

| 文件 | 组件 | 说明 |
|------|------|------|
| `Buttons.kt` | `CardButton` | 主要可点击区域（两个重载：文本+图标 / 标题+副标题+图标+箭头） |
| `Buttons.kt` | `PrimaryButton` | 通用主按钮，圆角 12dp，带阴影 |
| `Buttons.kt` | `CircleIconButton` | 圆形图标按钮，最小触摸目标 36dp |
| `Buttons.kt` | `ElegantButton` | 渐变填充+阴影按钮（仅 OtherScreen 使用） |
| `TopBar.kt` | `AppTopBar` | 顶部栏，带返回箭头、可选标题、可选溢出菜单（三个点） |
| `Container.kt` | `TitleCard` | 带彩色标题头的边框卡片 |
| `Container.kt` | `RowItem` | `Cell` 加权水平行，用于编辑表格 |
| `Text.kt` | `BoxText` | 彩色背景居中文本 |
| `Text.kt` | `CardText` | 卡片包裹的文本，默认左中对齐 |
| `Text.kt` | `CardTextFiled` | 卡片包裹的 `OutlinedTextField` 输入框 |
| `Dialog.kt` | `ConfirmDialog` | `AlertDialog` 封装，确认/取消回调 |
| `SwipeBox.kt` | `LeftSwipeBox` | 左滑手势容器，弹簧动画吸附，露出操作菜单 |

### 主题 (`ui/theme/`)

5 个 `ColorTheme` 枚举值（`Blue`、`Yellow`、`Green`、`Pink`、`Purple`），每个映射到一个 `lightColorScheme`，仅 `Purple` 有 `darkColorScheme`。

**主题数据流**:
```
用户选择颜色
  → SettingsScreen 回调
  → SettingsViewModel.saveColorTheme(theme)
  → SettingsRepository.saveColorTheme(theme)   // dataStore.edit { ... }
  → DataStore 持久化
  → SettingsRepository.colorThemeFlow 发射新值  // dataStore.data.map { ... }
  → MainActivity 中 collectAsState() 订阅
  → WhatToEatTheme(colorTheme) 重组
  → 全局主题切换
```

## ViewModel 层

### FoodViewModel — 核心业务逻辑

构造函数依赖 `FoodRepository`、`FoodTableRepository`、`SettingsRepository`。

**状态**（`StateFlow`，由 Compose 订阅）：
| 属性 | 类型 | 说明 |
|------|------|------|
| `tables` | `StateFlow<List<FoodTable>>` | 所有表格列表 |
| `currentTableId` | `StateFlow<Int>` | 当前选中表格 id（来自 DataStore） |
| `foods` | `StateFlow<List<Food>>` | 当前表格中的食物（通过 `flatMapLatest` 跟随 `currentTableId` 自动切换） |
| `chosenFood` | `Food?` | 上次随机选中的食物（普通属性，非响应式） |

**表格管理方法**: `switchTable(id)`、`createTable(name)`、`renameTable(id, newName)`、`deleteTable(id)`

**食物 CRUD**: `insert(food)`、`update(food)`、`delete(food)` — 均在 `viewModelScope.launch` 中执行 suspend 操作

**随机选择** (`chosenRandomFood()`): 同步方法，从 `foods.value` 过滤出 `marked && weight > 0 && name.isNotEmpty()` 的候选项，按权值加权随机，排除上次选中项（避免连续重复），返回选中的食物名

**辅助方法**: `ignoreChosenFood()`（标记 `marked=false`）、`clearAllIgnore()`（恢复全部 `marked=true`）

### SettingsViewModel — 主题管理

仅依赖 `SettingsRepository`，暴露 `colorTheme: StateFlow<ColorTheme>` 和 `saveColorTheme(theme)`。

## 模型层

`model/` 包包含非 Room 实体的纯数据类：

| 文件 | 内容 | 用途 |
|------|------|------|
| `model/model.kt` | `data class Cell(weight: Float, content: @Composable () -> Unit)` | 加权单元格，供 `RowItem` 使用 |
| `model/WebsiteItem.kt` | `data class WebsiteItem(title, subtitle, url, iconSource)` | 网站链接数据模型 |
| `model/PracticalWebsites.kt` | `val practicalWebsites: List<WebsiteItem>` | 硬编码 8 个实用网站 |

## 领域层

`domain/` 包目前仅含 `SelectFood.kt`，定义了一个**空壳函数** `selectFood(): Food?`（无实现）。实际的加权随机选择逻辑在 `FoodViewModel.chosenRandomFood()` 中同步完成。

## 关键设计模式

- **外层/内层拆分**: 页面拆为外部组合函数（ViewModel + 状态）和内部 `*Content` 组合函数（纯 UI + lambda 回调），便于 Preview 和单元测试
- **响应式多表切换**: `currentTableId.flatMapLatest { foodRepository.getByTableId(it) }` — 切换表格 id 时自动取消旧订阅、创建新订阅，无需手动调用刷新
- **写-读自动同步**: Room 的 `Flow` 在写操作（INSERT/UPDATE/DELETE）后自动发射新值，整个链路无需手动刷新 UI
- **透明 Repository**: Repository 层仅做方法转发（无业务逻辑转换），保持链路简洁；需要转换时在 ViewModel 层处理（如 `food.copy(tableId = currentTableId.value)`）
- **防误删二次确认**: 编辑表格中删除操作通过 `ConfirmDialog` 确认，`LeftSwipeBox` 左滑露出操作菜单
- **DataStore 主题持久化**: `MainActivity` 订阅 `colorThemeFlow` 并在顶层重组，保证主题全局生效且即时切换

## 禁止执行的危险操作

以下操作在任何情况下都**禁止**执行，除非用户明确以文字形式确认（"我知道这个操作的风险，请执行"）：

### 数据库

- **禁止**修改 Room 实体（`@Entity` 类）的字段名、类型或删除字段，除非同时创建了正确的 `Migration` 并递增 `version`
- **禁止**删除、修改或绕过现有的数据库迁移（如 `MIGRATION_1_2`）—— 已有用户的设备上运行着旧版本数据库
- **禁止**使用 `fallbackToDestructiveMigration()` —— 会导致所有用户数据丢失
- **禁止**手动删除或清空数据库文件

### DataStore

- **禁止**修改 `Settings.kt` 中已存在的偏好键名（`"color_theme"`、`"current_table_id"`）—— 已有用户的偏好会丢失
- **禁止**修改 `Context.dataStore` 的文件名（`"settings"`）—— 会创建一个全新的空 DataStore，原有偏好全部丢失
- **禁止**删除 `SettingsRepository` 中的现有键而不提供迁移逻辑

### 架构

- **禁止**引入任何 DI 框架（Hilt、Koin 等）—— 本项目采用手动依赖注入，引入框架会破坏现有架构
- **禁止**将 `FoodViewModel` 的构造函数参数从三个仓库改为其他形式，除非同步更新 `MainScreen.kt` 中的 `viewModelFactory`
- **禁止**修改 `MainApplication` 中仓库的初始化方式（`by lazy` 单例模式），除非有明确理由并经过审查
- **禁止**将 `Cell.content` lambda 存储到非 `@Composable` 上下文中 —— 会导致运行时崩溃

### 文件系统

- **禁止**直接执行 `rm`、`rm -rf`、`del` 等删除命令 —— 删除文件必须使用 Write/Edit 工具或让用户手动操作，防止误删
- **禁止**在项目根目录外执行任何文件操作

### 版本控制

- **禁止**执行 `git push --force` 到 `main` 分支
- **禁止**执行 `git reset --hard` 删除已提交的代码，除非用户明确要求
- **禁止**修改 `.gitignore` 将关键源文件排除在版本控制之外
- **禁止**提交包含硬编码密钥、Token 或密码的代码

### 构建系统

- **禁止**修改 `applicationId`（`me.normal.whattoeat`）—— 会导致应用被视为全新应用，无法覆盖安装
- **禁止**升级 Room 或 Compose 的主版本号（如 2.x → 3.x）而不经过用户同意 —— API 可能不兼容
- **禁止**删除或重命名 `app/build.gradle.kts` 中的关键配置块

### UI 与用户体验

- **禁止**删除 `ConfirmDialog` 确认逻辑并将删除操作改为直接执行 —— 现有的防误删设计是有意为之
- **禁止**移除 `FoodEditScreen` 中左滑删除的二次确认（`LeftSwipeBox` + `ConfirmDialog`）
- **禁止**修改底部导航栏的"首页"和"设置"两个 tab 的顺序或目标路由
- **禁止**将应用界面语言从中文改为其他语言，除非用户明确要求

### 运行时

- **禁止**在 `MainActivity` 以外的组件中修改全局主题状态 —— 主题切换必须通过 `SettingsViewModel.saveColorTheme()` 完成
- **禁止**在 `viewModelScope` 外的协程中调用 Repository 的 `suspend` 函数 —— 可能导致协程泄漏
- **禁止**将 `StateFlow` 降级为普通 `Flow` 或 `LiveData` —— 会破坏响应式数据链
