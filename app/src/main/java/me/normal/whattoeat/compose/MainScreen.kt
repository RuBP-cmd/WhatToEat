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
import androidx.navigation.compose.rememberNavController
import me.normal.whattoeat.compose.Home.HomeScreen
import me.normal.whattoeat.compose.Settings.SettingsScreen
import me.normal.whattoeat.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreen(){
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isVisable by remember { mutableStateOf(true) }
    Scaffold(
        modifier = Modifier,
        topBar = {
            CenterAlignedTopAppBar(title = { Text("WhatToEat v1.0") })
        },
        bottomBar = {
            MainScreenNav(
                isVisable,
                selectedIndex,
                {newSelectedIndex ->
                    selectedIndex = newSelectedIndex
                }
            )
        }
    ){ paddingValues ->
        Box(
            Modifier.padding(paddingValues)
        ){
            when(selectedIndex){
                0 -> HomeScreen()
                1 -> SettingsScreen()
            }
        }
    }

}

@Composable
fun MainScreenNav(
    isVisable: Boolean,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
){

    if(isVisable){ // 用于展示或者隐藏导航栏
        NavigationBar() {
            NavigationBarItem(
                selected = selectedIndex == 0,
                onClick = { onItemSelected(0) }, // 按钮按下后将selectedIndex更新
                label = { Text("首页") },         // 然后便会自动切换页面和导航栏重绘
                icon = {
                    Icon(
                        imageVector = if(selectedIndex == 0)  Icons.Filled.Home else Icons.Outlined.Home, // 选中时变为实心，否则空心
                        contentDescription = null // 由于已经有了label，就不需要描述图片了，防止重复播报
                    )
                }
            )
            NavigationBarItem(
                selected = selectedIndex == 1,
                onClick = { onItemSelected(1) },
                label = { Text("设置") },
                icon = {
                    Icon(
                        imageVector = if(selectedIndex == 1) Icons.Filled.Settings else Icons.Outlined.Settings,
                        contentDescription = null
                    )
                }
            )
        }
    }

}




