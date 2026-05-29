package me.normal.whattoeat.compose


import android.R.attr.data
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
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
        bottomBar = { MainScreenNav(navController) }
    ){ paddingValues ->
        Box(
            Modifier.padding(paddingValues)
        ){
            NavHost(navController, Home){
                composable<Home>{ HomeScreen{ navController.navigate(Eat)} } // Home -> Eat
                composable<Settings>{ SettingsScreen() }
                composable<Eat>{ EatScreen( // Home <- Eat -> FoodEdit
                    onNavigateToFoodEdit = { navController.navigate(FoodEdit) },
                    onReturnToHome = { navController.popBackStack() }
                ) }
                composable<FoodEdit>{ FoodEditScreen{ navController.popBackStack()} } // Eat <- FoodEdit
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
        data class Item(
            val label: String,
            val selected: Boolean,
            val FilledVector: ImageVector,
            val OutlinedVector: ImageVector,
            val route: Any
        )

        val itemList = listOf(
            Item("首页", selectedHome, Icons.Filled.Home, Icons.Outlined.Home, Home),
            Item("设置", selectedSettings, Icons.Filled.Settings, Icons.Outlined.Settings, Settings)
        )

        NavigationBar() {
            for (item in itemList){
                NavigationBarItem(
                    selected = item.selected,
                    onClick = {
                        navController.navigate(item.route){
                            popUpTo(navController.graph.findStartDestination().id){
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    label = { Text(
                        item.label,
                        color = if(item.selected) MaterialTheme.colorScheme.primary else Color.Unspecified
                    ) },
                    icon = {
                        Icon(
                            imageVector = if(item.selected) item.FilledVector else item.OutlinedVector,
                            contentDescription = null,
                            tint = if(item.selected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                )
            }

        }
    }

}




