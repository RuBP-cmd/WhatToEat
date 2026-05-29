package me.normal.whattoeat.compose


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import me.normal.whattoeat.compose.home.HomeScreen
import me.normal.whattoeat.compose.misc.EatScreen
import me.normal.whattoeat.compose.misc.FoodEditScreen
import me.normal.whattoeat.compose.settings.SettingsScreen

@Serializable
object Home
@Serializable
object Settings
@Serializable
object FoodEdit
@Serializable
object Eat


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreen(){
    val navController = rememberNavController();


    Scaffold(
        modifier = Modifier,
        topBar = {
            CenterAlignedTopAppBar(title = { Text("WhatToEat v1.0") })
        },
        bottomBar = { MainScreenNav(navController) }
    ){ paddingValues ->
        Box(
            Modifier.padding(paddingValues)
        ){
            NavHost(navController, Home){
                composable<Home>{ HomeScreen{ navController.navigate(Eat) } }
                composable<Settings>{ SettingsScreen() }
                composable<Eat>{ EatScreen() }
                composable<FoodEdit>{ FoodEditScreen() }
            }
        }
    }

}

@Composable
fun MainScreenNav(
    navController: NavController
){
    val navBackStackEntry by navController.currentBackStackEntryAsState() // 获取State<NavBackStateEntry>使得能在改变的时候被监听
    val currentDestination = navBackStackEntry?.destination

    val selectedHome = currentDestination?.hasRoute<Home>() == true
    val selectedSettings = currentDestination?.hasRoute<Settings>() == true
    val isVisible = selectedHome || selectedSettings

    if(isVisible){ // 用于展示或者隐藏导航栏
        NavigationBar() {
            NavigationBarItem(
                selected = selectedHome,
                onClick = {
                    navController.navigate(Home){
                        popUpTo(navController.graph.startDestinationId){
                            saveState = true
                        }
                        launchSingleTop = true // 顶部页面不重复入栈
                        restoreState = true // 恢复保存的状态
                    }
                          }, // 按钮按下后将selectedIndex更新
                label = { Text("首页") },         // 然后便会自动切换页面和导航栏重绘
                icon = {
                    Icon(
                        imageVector = if(selectedHome)  Icons.Filled.Home else Icons.Outlined.Home, // 选中时变为实心，否则空心
                        contentDescription = null // 由于已经有了label，就不需要描述图片了，防止重复播报
                    )
                }
            )
            NavigationBarItem(
                selected = selectedSettings,
                onClick = {
                    navController.navigate(Settings){
                        popUpTo(navController.graph.startDestinationId){
                            saveState = true
                        }
                        launchSingleTop = true // 顶部页面不重复入栈
                        restoreState = true // 恢复保存的状态
                    } // 改页面
                          },
                label = { Text("设置") },
                icon = {
                    Icon(
                        imageVector = if(selectedSettings) Icons.Filled.Settings else Icons.Outlined.Settings,
                        contentDescription = null
                    )
                }
            )
        }
    }

}




