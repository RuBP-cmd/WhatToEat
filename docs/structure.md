#### 整体架构

  采用单 Activity 架构，MainActivity 是唯一入口，页面之间通过 Jetpack Navigation Compose 进行导航。无 DI 框架，通过
  MainApplication 手动管理依赖。

#### 构建配置

  - 语言：Kotlin
  - UI 框架：Jetpack Compose + Material 3
  - 编译 SDK：37，最低支持 Android 7.0 (API 24)
  - 关键依赖：Room（数据库）、DataStore（键值配置）、Navigation Compose（路由）、Glide Compose（图片加载）
  - 版本目录统一管理在 gradle/libs.versions.toml



#### 项目结构

- data 
  - local 数据库
    - database：Room 数据库（AppDatabase），双重检查锁单例
    - dao：FoodDao，提供增删改查
    - entry：Food 实体（id、时间戳、名称、权重、是否参选）
    - settings：DataStore 扩展属性（键名 "settings"），用于持久化偏好设置
  - repository
    - FoodRepository：封装 FoodDao，返回 Flow 和 suspend 操作
    - SettingsRepository：封装 DataStore，读写颜色主题

- model 定义的一些数据类型，集中存放于此包中
  - Cell：包含 Composable 内容和 weight 权重，用于 RowItem 表格行布局

- ui UI层
  - components 自定义的一些组件（Composable函数），复用
    - CardButton：可点击卡片按钮（支持图标、主副标题、导航箭头）
    - AppTopBar：顶部栏（返回箭头 + 标题 + 可选更多按钮）
    - TitleCard：带标题头的卡片容器
    - RowItem：基于 Cell 权重的水平行布局
    - BoxText：居中文本
    - ElegantButton：渐变按钮（仅 OtherScreen 使用）
    - AppIconButton：48dp 最小触摸区域的图标按钮
  - screens 各大页面
    - MainScreen：路由表（6 个序列化路由）+ 底部导航栏（仅 Home/Settings 显示）
    - home/HomeScreen：首页入口（Moba卡片 + 4 个功能按钮）
    - food/EatScreen：随机选食物（查询/清除/忽略/恢复），加权随机算法，排除上次结果
    - food/FoodEditScreen：食物列表编辑（LazyColumn + 粘性表头，弹出行操作栏）
    - settings/SettingsScreen：颜色主题选择器 + 软件信息（版本号 + GitHub 链接）
    - misc/PracticalWebsiteScreen：实用网站列表（一站式、电费、选课、B站等）
    - misc/OtherScreen：彩蛋页（点击 25 次后显示 moba 图片）
  - theme 主题，包含颜色与字体
    - Color：颜色常量定义
    - Type：字体排版
    - Theme：5 种 ColorTheme 枚举（蓝/黄/绿/粉/紫，仅 Purple 有深色主题）+ WhatToEatTheme
  - viewmodel
    - FoodViewModel：食物 CRUD + 加权随机选择，StateFlow 订阅 Room 数据
    - SettingsViewModel：颜色主题的读取与保存，StateFlow 订阅 DataStore
