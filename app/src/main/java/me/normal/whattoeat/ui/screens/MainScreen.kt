package me.normal.whattoeat.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import me.normal.whattoeat.MainApplication
import me.normal.whattoeat.ui.screens.home.HomeScreen
import me.normal.whattoeat.ui.screens.food.EatScreen
import me.normal.whattoeat.ui.screens.food.FoodEditScreen
import me.normal.whattoeat.ui.screens.misc.OtherScreen
import me.normal.whattoeat.ui.screens.misc.PracticalWebsiteScreen
import me.normal.whattoeat.ui.screens.settings.SettingsScreen
import me.normal.whattoeat.ui.viewmodel.FoodViewModel

@Serializable
object Home
@Serializable
object Settings
@Serializable
object FoodEdit
@Serializable
object Eat
@Serializable
object PracticalWebsite
@Serializable
object Other


//@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreen(){
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as MainApplication // 仅临时用
    val foodViewModel: FoodViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                FoodViewModel(application.foodRepository, application.foodTableRepository)
            }
        }
    )

    Scaffold(
        modifier = Modifier,
        bottomBar = { MainScreenNav(navController) }
    ){ paddingValues ->
        Box(
            Modifier.padding(paddingValues)
        ){
            NavHost(navController, Home){
                composable<Home>{ HomeScreen(
                    onNavigateToEat = { navController.navigate(Eat)}, // Home -> Eat
                    onNavigateToPracticalWebsite = { navController.navigate(PracticalWebsite) }, // Home -> PracticalWebsite
                    onNavigateToOther = { navController.navigate(Other) }
                ) }
                composable<Settings>{ SettingsScreen() }
                composable<Eat>{ EatScreen( // Home <- Eat -> FoodEdit
                    foodViewModel = foodViewModel,
                    onNavigateToFoodEdit = { navController.navigate(FoodEdit) },
                    onReturnToHome = { navController.popBackStack() }
                ) }
                composable<FoodEdit>{ FoodEditScreen(
                    foodViewModel = foodViewModel,
                    onReturnToEat = { navController.popBackStack()}
                ) } // Eat <- FoodEdit
                composable<PracticalWebsite>{ PracticalWebsiteScreen { navController.popBackStack() } }
                composable<Other>{ OtherScreen{ navController.popBackStack() } }
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
            val filledVector: ImageVector,
            val outlinedVector: ImageVector,
            val route: Any
        )

        val itemList = listOf(
            Item("首页", selectedHome, Icons.Filled.Home, Icons.Outlined.Home, Home),
            Item("设置", selectedSettings, Icons.Filled.Settings, Icons.Outlined.Settings, Settings)
        )

        NavigationBar {
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
                            imageVector = if(item.selected) item.filledVector else item.outlinedVector,
                            contentDescription = null,
                            tint = if(item.selected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                )
            }

        }
    }

}




