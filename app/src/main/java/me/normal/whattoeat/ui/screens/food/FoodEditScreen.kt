package me.normal.whattoeat.ui.screens.food

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.R
import me.normal.whattoeat.data.local.entry.Food
import me.normal.whattoeat.data.local.entry.FoodTable
import me.normal.whattoeat.model.Cell
import me.normal.whattoeat.ui.components.AppTopBar
import me.normal.whattoeat.ui.components.CircleIconButton
import me.normal.whattoeat.ui.components.PrimaryButton
import me.normal.whattoeat.ui.components.RowItem
import me.normal.whattoeat.ui.viewmodel.FoodViewModel

@Composable
fun FoodEditScreen(
    foodViewModel: FoodViewModel,
    onReturnToEat: () -> Unit
) {
    val foodList by foodViewModel.foods.collectAsState(initial = emptyList())
    val tables by foodViewModel.tables.collectAsState()
    val currentTableId by foodViewModel.currentTableId.collectAsState()

    FoodEditContent(
        foodList = foodList,
        tables = tables,
        currentTableId = currentTableId,
        onReturnToEat = onReturnToEat,
        onTableSelected = { foodViewModel.switchTable(it) },
        onRenameTable = { tableId, name -> foodViewModel.renameTable(tableId, name) },
        onDeleteTable = { tableId -> foodViewModel.deleteTable(tableId) },
        onCreateTable = { name -> foodViewModel.createTable(name) },
        onClickAddRow = { foodViewModel.insert(Food(name = "", weight = 1, marked = true)) },
        onClickDelRow = { food -> foodViewModel.delete(food) },
        onClickStar = { food -> foodViewModel.update(food.copy(marked = !food.marked)) },
        onInputName = { food, name -> foodViewModel.update(food.copy(name = name)) },
        onInputWeight = { food, weight -> foodViewModel.update(food.copy(weight = weight)) }
    )
}

@Composable
fun FoodEditContent(
    foodList: List<Food>,
    tables: List<FoodTable>,
    currentTableId: Int,
    onReturnToEat: () -> Unit,
    onTableSelected: (Int) -> Unit,
    onRenameTable: (Int, String) -> Unit,
    onDeleteTable: (Int) -> Unit,
    onCreateTable: (String) -> Unit,
    onClickAddRow: () -> Unit,
    onClickDelRow: (food: Food) -> Unit,
    onClickStar: (food: Food) -> Unit,
    onInputName: (food: Food, name: String) -> Unit,
    onInputWeight: (food: Food, weight: Int) -> Unit
) {
    var isDeleteMode by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newTableName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { AppTopBar(onReturnToEat, "编辑清单") }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // 编辑表
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 30.dp, end = 50.dp, top = 100.dp, bottom = 80.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                EditTable(
                    foodList = foodList,
                    isDeleteMode = isDeleteMode,
                    onClickStar = onClickStar,
                    onInputName = onInputName,
                    onInputWeight = onInputWeight,
                    onClickDelRow = onClickDelRow,
                )
            }


            // 简略书签侧栏（右侧）
            EditBookmarkSidebar(
                tables = tables,
                currentTableId = currentTableId,
                onTableSelected = onTableSelected,
                onRenameTable = onRenameTable,
                onDeleteTable = onDeleteTable,
                onAddTable = { showCreateDialog = true },
                modifier = Modifier.align(Alignment.CenterEnd)
            )

            // 底部按钮
            Row(
                modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ){
                PrimaryButton("添加新菜品", Modifier.weight(2f).height(48.dp)) { onClickAddRow() }
                PrimaryButton(
                    "编辑",
                    Modifier.weight(1f).height(48.dp),
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.primary)
                { isDeleteMode = !isDeleteMode }
            }
        }
    }

    // 弹出对话框
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                newTableName = ""
            },
            title = { Text("新建表格") },
            text = {
                OutlinedTextField(
                    value = newTableName,
                    onValueChange = { newTableName = it },
                    label = { Text("表格名称") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCreateTable(newTableName.trim())
                        newTableName = ""
                        showCreateDialog = false
                    },
                    enabled = newTableName.isNotBlank()
                ) { Text("创建") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateDialog = false
                    newTableName = ""
                }) { Text(text = "取消", color = MaterialTheme.colorScheme.onSurface) }
            }
        )
    }
}



@Composable
private fun EditTable(
    foodList: List<Food>,
    isDeleteMode: Boolean,
    onClickStar: (food: Food) -> Unit,
    onInputName: (food: Food, name: String) -> Unit,
    onInputWeight: (food: Food, weight: Int) -> Unit,
    onClickDelRow: (food: Food) -> Unit
) {
    val weightList = listOf(2f, 8f, 3f)
    val titleColor = MaterialTheme.colorScheme.primary
    val titleStyle = MaterialTheme.typography.titleMedium
    val textStyle = MaterialTheme.typography.bodyMedium
    val horizontalPaddingValues = 5.dp
    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        stickyHeader {
            RowItem(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = horizontalPaddingValues),
                cells = listOf(
                    Cell({ Text(text = "参选", color = titleColor, style = titleStyle) }, weightList[0]),
                    Cell({ Text(text = "名称", color = titleColor, style = titleStyle) }, weightList[1]),
                    Cell({ Text(text = "权重", color = titleColor, style = titleStyle) }, weightList[2])
                )
            )
        }

        items(foodList, key = { it.id }) { food ->
            var isError by remember { mutableStateOf(false) }
            var foodName by remember(food.id) { mutableStateOf(TextFieldValue(food.name)) }
            var foodWeight by remember(food.id) { mutableStateOf(TextFieldValue(food.weight.toString())) }


            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
            ) {
                RowItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(horizontal = horizontalPaddingValues),
                    cells = listOf(
                        Cell( // 参选star
                            content = {
                                IconButton({ onClickStar(food) }) {
                                    Image(
                                        painter = painterResource(if (food.marked) R.drawable.filled_star else R.drawable.outlined_star),
                                        contentDescription = "参选"
                                    )
                                }
                            },
                            weight = weightList[0]
                        ),
                        Cell( // 名称name输入
                            content = {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ){
                                    OutlinedTextField(
                                        value = foodName,
                                        textStyle = textStyle,
                                        colors = textFieldColors,
                                        placeholder = { Text(text = "请输入名称", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                        onValueChange = { newFoodName ->
                                            foodName = newFoodName
                                            onInputName(food, newFoodName.text)
                                        }
                                    )
                                }

                            },
                            weight = weightList[1]
                        ),
                        Cell( // 权重weight输入
                            content = {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ){
                                    OutlinedTextField(
                                        value = foodWeight,
                                        textStyle = textStyle,
                                        colors = textFieldColors,
                                        onValueChange = { newFoodWeight ->
                                            foodWeight = newFoodWeight
                                            val text = newFoodWeight.text
                                            if (text.isNotEmpty() && text.length <= 5 && text.all { it.isDigit() }) {
                                                onInputWeight(food, text.toInt())
                                                isError = false
                                            } else {
                                                isError = text.isNotEmpty()
                                            }
                                        },
                                        isError = isError,
                                        supportingText = if (isError) {
                                            { Text("请输入有效数字", color = MaterialTheme.colorScheme.error) }
                                        } else null,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    )
                                }

                            },
                            weight = weightList[2]
                        )
                    )
                )

                if (isDeleteMode) {
                    CircleIconButton(
                        { onClickDelRow(food) },
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.End)
                            .offset(x = (-2).dp, y = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "删除行"
                        )
                    }
                }

            }
        }
    }
}

@Preview
@Composable
fun FoodEditContentPreview() {
    FoodEditContent(
        foodList = emptyList(),
        tables = listOf(
            FoodTable(1, "默认", 0),
            FoodTable(2, "午餐", 1)
        ),
        currentTableId = 1,
        onReturnToEat = {},
        onTableSelected = {},
        onRenameTable = { _, _ -> },
        onDeleteTable = {},
        onCreateTable = {},
        onClickAddRow = {},
        onClickDelRow = {},
        onClickStar = { },
        onInputName = { _, _ -> },
        onInputWeight = { _, _ -> },
    )
}
