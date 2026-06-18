package me.normal.whattoeat.ui.screens.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.data.local.entry.FoodTable
import me.normal.whattoeat.ui.components.AppTopBar
import me.normal.whattoeat.ui.components.CardButton
import me.normal.whattoeat.ui.components.CircleIconButton
import me.normal.whattoeat.ui.viewmodel.FoodViewModel

@Composable
fun EatScreen(
    foodViewModel: FoodViewModel,
    onNavigateToFoodEdit: () -> Unit,
    onReturnToHome: () -> Unit
) {
    val tables by foodViewModel.tables.collectAsState()
    val currentTableId by foodViewModel.currentTableId.collectAsState()
    var foodName by remember { mutableStateOf("点击查询今天吃什么") }

    EatContent(
        foodName = foodName,
        tables = tables,
        currentTableId = currentTableId,
        onNavigateToFoodEdit = onNavigateToFoodEdit,
        onReturnToHome = onReturnToHome,
        onTableSelected = { tableId ->
            foodViewModel.switchTable(tableId)
            foodName = "点击查询今天吃什么"
        },
        onClickRandomFood = { foodName = foodViewModel.chosenRandomFood() },
        onClickClear = { foodName = "点击查询今天吃什么" },
        onClickIgnore = { foodViewModel.ignoreChosenFood() },
        onClickClearIgnore = { foodViewModel.clearAllIgnore() }
    )
}

@Composable
private fun EatContent(
    foodName: String,
    tables: List<FoodTable>,
    currentTableId: Int,
    onNavigateToFoodEdit: () -> Unit,
    onReturnToHome: () -> Unit,
    onTableSelected: (Int) -> Unit,
    onClickRandomFood: () -> Unit,
    onClickClear: () -> Unit,
    onClickIgnore: () -> Unit,
    onClickClearIgnore: () -> Unit
) {
    Scaffold(
        topBar = { AppTopBar(onReturnToHome, "Eat", onNavigateToFoodEdit) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(50.dp)
            ) {
                Card(
                    modifier = Modifier
                        .width(250.dp)
                        .height(70.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = foodName,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val width = 150.dp
                    val height = 55.dp
                    val modifier = Modifier
                        .width(width)
                        .height(height)

                    CardButton(
                        text = "查询",
                        modifier = modifier,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.QueryStats,
                                contentDescription = "查询"
                            )
                        }
                    ) { onClickRandomFood() }
                    CardButton(
                        text = "清除",
                        modifier = modifier,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "清除"
                            )
                        }
                    ) { onClickClear() }
                    CardButton(
                        text = "忽略",
                        modifier = modifier,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Block,
                                contentDescription = "忽略"
                            )
                        }
                    ) { onClickIgnore() }
                    CardButton(
                        text = "恢复",
                        modifier = modifier,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.ClearAll,
                                contentDescription = "恢复"
                            )
                        }
                    ) { onClickClearIgnore() }
                }
            }

            // 书签侧栏（右侧）
            BookmarkSidebar(
                tables = tables,
                currentTableId = currentTableId,
                onTableSelected = onTableSelected,
                modifier = Modifier.align(Alignment.CenterEnd)
            )

            // 编辑按钮
            CircleIconButton(
                onClick = onNavigateToFoodEdit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = 150.dp)
            ) {
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
private fun EatContentPreview() {
    EatContent(
        foodName = "显示一个食物名称",
        tables = listOf(
            FoodTable(1, "默认", 0),
            FoodTable(2, "午餐", 1),
            FoodTable(3, "晚餐", 2)
        ),
        currentTableId = 1,
        onNavigateToFoodEdit = {},
        onReturnToHome = {},
        onTableSelected = {},
        onClickRandomFood = {},
        onClickClear = {},
        onClickIgnore = {},
        onClickClearIgnore = {}
    )
}
