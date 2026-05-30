package me.normal.whattoeat.ui.screens.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.ui.components.AppIconButton
import me.normal.whattoeat.ui.components.ElegantButton
import me.normal.whattoeat.ui.components.TopBar
import me.normal.whattoeat.ui.viewmodel.FoodViewModel


@Composable
fun EatScreen(
    foodViewModel: FoodViewModel,
    onNavigateToFoodEdit: () -> Unit,
    onReturnToHome: () -> Unit
){
    // 数据
    var foodName by remember { mutableStateOf("点击查询今天吃什么") }

    EatContent( // 纯ui
        onNavigateToFoodEdit = onNavigateToFoodEdit,
        onReturnToHome = onReturnToHome,
        onClickRandomFood = { foodName = foodViewModel.choosenRandomFood() },
        foodName = foodName
    )

}


@Composable
private fun EatContent(
    onNavigateToFoodEdit: () -> Unit,
    onReturnToHome: () -> Unit,
    onClickRandomFood: () -> Unit,
    foodName: String
){
    Scaffold(
        topBar = { TopBar(onReturnToHome, "Eat", onNavigateToFoodEdit) }
    ){ paddingValues ->

        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ){
            Column(
                modifier = Modifier.fillMaxSize().padding(vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ){
                Card(
                    modifier = Modifier.width(250.dp).height(70.dp),
                ){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = foodName,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                }
                ElegantButton("点击查询"){ onClickRandomFood() }
            }

            // 叠加在右边

            AppIconButton(
                onClick = onNavigateToFoodEdit,
                modifier = Modifier.align(Alignment.CenterEnd)
            ){
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "编辑"
                )
            }
        }

    }
}

@Preview
@Composable
private fun EatContentPreview(){
    EatContent(
        {},
        {},
        {},
        "显示一个食物名称"
    )
}