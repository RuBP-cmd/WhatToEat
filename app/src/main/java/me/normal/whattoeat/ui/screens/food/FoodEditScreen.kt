package me.normal.whattoeat.ui.screens.food

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.R
import me.normal.whattoeat.data.local.entry.Food
import me.normal.whattoeat.data.local.entry.FoodTable
import me.normal.whattoeat.model.Cell
import me.normal.whattoeat.ui.components.AppIconButton
import me.normal.whattoeat.ui.components.AppTopBar
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
    var isChooseBarShown by remember { mutableStateOf(false) }
    var isDeleteMode by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newTableName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { AppTopBar(onReturnToEat, "编辑清单") { isChooseBarShown = !isChooseBarShown } }
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

            // 弹出栏
            if (isChooseBarShown) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-1).dp, y = 3.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(18.dp)
                        )
                ) {
                    AppIconButton(onClickAddRow) {
                        Icon(
                            imageVector = Icons.Filled.AddCircleOutline,
                            contentDescription = "添加行"
                        )
                    }
                    AppIconButton({ isDeleteMode = !isDeleteMode }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "编辑"
                        )
                    }
                }
            }

            // 简略书签侧栏（右侧）
            SimpleBookmarkSidebar(
                tables = tables,
                currentTableId = currentTableId,
                onTableSelected = onTableSelected,
                onRenameTable = onRenameTable,
                onDeleteTable = onDeleteTable,
                onAddTable = { showCreateDialog = true },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }

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
                }) { Text("取消") }
            }
        )
    }
}

// --- 简略书签侧栏 ---

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SimpleBookmarkSidebar(
    tables: List<FoodTable>,
    currentTableId: Int,
    onTableSelected: (Int) -> Unit,
    onRenameTable: (Int, String) -> Unit,
    onDeleteTable: (Int) -> Unit,
    onAddTable: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 长按菜单状态
    var contextMenuTableId by remember { mutableIntStateOf(-1) }
    // 重命名对话框状态
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameTableId by remember { mutableIntStateOf(-1) }
    var renameText by remember { mutableStateOf("") }
    // 删除确认对话框状态
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteTableId by remember { mutableIntStateOf(-1) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 100.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.End
    ) {
        tables.forEach { table ->
            val isActive = table.id == currentTableId

            Box {
                Surface(
                    modifier = Modifier
                        .width(44.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                        .combinedClickable(
                            onClick = { onTableSelected(table.id) },
                            onLongClick = { contextMenuTableId = table.id }
                        ),
                    shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp),
                    color = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = table.name.take(2),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 长按弹出菜单
                DropdownMenu(
                    expanded = contextMenuTableId == table.id,
                    onDismissRequest = { contextMenuTableId = -1 }
                ) {
                    DropdownMenuItem(
                        text = { Text("重命名") },
                        onClick = {
                            contextMenuTableId = -1
                            renameTableId = table.id
                            renameText = table.name
                            showRenameDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("删除") },
                        onClick = {
                            contextMenuTableId = -1
                            deleteTableId = table.id
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        // 新建表格按钮
        AppIconButton(
            onClick = onAddTable,
            modifier = Modifier
                .padding(end = 4.dp)
                .size(28.dp)
                .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "新建表格",
                modifier = Modifier.size(14.dp)
            )
        }
    }

    // --- 重命名对话框 ---
    if (showRenameDialog) {
        var localText by remember(renameTableId) { mutableStateOf(renameText) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("重命名表格") },
            text = {
                OutlinedTextField(
                    value = localText,
                    onValueChange = { localText = it },
                    label = { Text("表格名称") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRenameTable(renameTableId, localText.trim())
                        showRenameDialog = false
                    },
                    enabled = localText.isNotBlank()
                ) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("取消") }
            }
        )
    }

    // --- 删除确认对话框 ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除表格") },
            text = { Text("确定要删除这个表格吗？\n表格内的所有食物也将被删除。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTable(deleteTableId)
                        showDeleteDialog = false
                    }
                ) { Text("删除") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("取消") }
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
    val headColor = MaterialTheme.colorScheme.primary
    val titleColor = MaterialTheme.colorScheme.onPrimary
    val bodyColor = MaterialTheme.colorScheme.secondary
    val titleStyle = MaterialTheme.typography.titleMedium
    val textStyle = MaterialTheme.typography.bodyMedium
    val horizontalPaddingValues = 5.dp
    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = bodyColor),
    ) {
        stickyHeader {
            RowItem(
                modifier = Modifier
                    .background(headColor)
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

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                RowItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(horizontal = horizontalPaddingValues),
                    cells = listOf(
                        Cell(
                            content = {
                                AppIconButton({ onClickStar(food) }) {
                                    Image(
                                        painter = painterResource(if (food.marked) R.drawable.filled_star else R.drawable.outlined_star),
                                        contentDescription = "参选"
                                    )
                                }
                            },
                            weight = weightList[0]
                        ),
                        Cell(
                            content = {
                                TextField(
                                    value = foodName,
                                    textStyle = textStyle,
                                    colors = textFieldColors,
                                    placeholder = { Text(text = "请输入名称", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    onValueChange = { newFoodName ->
                                        foodName = newFoodName
                                        onInputName(food, newFoodName.text)
                                    }
                                )
                            },
                            weight = weightList[1]
                        ),
                        Cell(
                            content = {
                                TextField(
                                    value = foodWeight,
                                    textStyle = textStyle,
                                    colors = textFieldColors,
                                    onValueChange = { newFoodWeight ->
                                        if (newFoodWeight.text.length in 1..5 && newFoodWeight.text.all { it.isDigit() }) {
                                            foodWeight = newFoodWeight
                                            onInputWeight(food, newFoodWeight.text.toInt())
                                            isError = false
                                        } else isError = true
                                    },
                                    isError = isError,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                )
                            },
                            weight = weightList[2]
                        )
                    )
                )

                if (isDeleteMode) {
                    AppIconButton(
                        { onClickDelRow(food) },
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-2).dp, y = 2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = CircleShape
                            )
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
        onInputName = { food, name -> },
        onInputWeight = { food, weight -> },
    )
}
