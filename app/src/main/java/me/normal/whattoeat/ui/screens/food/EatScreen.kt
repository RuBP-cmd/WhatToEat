package me.normal.whattoeat.ui.screens.food

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.data.local.entry.FoodTable
import me.normal.whattoeat.ui.components.AppIconButton
import me.normal.whattoeat.ui.components.AppTopBar
import me.normal.whattoeat.ui.components.CardButton
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
        onCreateTable = { name -> foodViewModel.createTable(name) },
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
    onCreateTable: (String) -> Unit,
    onClickRandomFood: () -> Unit,
    onClickClear: () -> Unit,
    onClickIgnore: () -> Unit,
    onClickClearIgnore: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

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
                onAddTable = { showCreateDialog = true },
                modifier = Modifier.align(Alignment.CenterEnd)
            )

            // 编辑按钮
            AppIconButton(
                onClick = onNavigateToFoodEdit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = 150.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "编辑"
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateTableDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = {
                onCreateTable(it)
                showCreateDialog = false
            }
        )
    }
}

// --- 书签侧栏 ---

@Composable
private fun BookmarkSidebar(
    tables: List<FoodTable>,
    currentTableId: Int,
    onTableSelected: (Int) -> Unit,
    onAddTable: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedTableId by remember { mutableIntStateOf(-1) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 100.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.End
    ) {
        tables.forEach { table ->
            BookmarkItem(
                name = table.name,
                isActive = table.id == currentTableId,
                isExpanded = table.id == expandedTableId,
                onClick = {
                    if (expandedTableId == table.id) {
                        // 已展开，再次点击：切换到此表
                        expandedTableId = -1
                    } else {
                        // 未展开：展开显示完整表名
                        expandedTableId = table.id
                    }
                    onTableSelected(table.id)
                }
            )
        }

        // 新建表格按钮
        AppIconButton(
            onClick = {
                expandedTableId = -1
                onAddTable()
            },
            modifier = Modifier
                .padding(end = 2.dp)
                .size(32.dp)
                .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "新建表格",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun BookmarkItem(
    name: String,
    isActive: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val collapsedWidth = 36.dp
    // 根据文字长度估算展开宽度（每个中文字符约 16dp）
    val expandedWidthDp = with(LocalDensity.current) {
        // 中文一个字约 1em = bodySmall 字号 ≈ 12sp，加上 padding
        val charCount = name.length.coerceIn(2, 8)
        (charCount * 16 + 16).coerceIn(56, 160).dp
    }
    val width by animateDpAsState(
        targetValue = if (isExpanded) expandedWidthDp else collapsedWidth,
        animationSpec = tween(durationMillis = 250),
        label = "bookmarkWidth"
    )

    Surface(
        modifier = Modifier
            .width(width)
            .height(40.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
        color = if (isActive) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 6.dp, end = 6.dp)
        ) {
            Text(
                text = if (isExpanded) name else name.take(2),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isActive) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- 新建表格对话框 ---

@Composable
private fun CreateTableDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tableName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建表格") },
        text = {
            OutlinedTextField(
                value = tableName,
                onValueChange = { tableName = it },
                label = { Text("表格名称") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(tableName.trim()) },
                enabled = tableName.isNotBlank()
            ) {
                Text("创建")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
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
        onCreateTable = {},
        onClickRandomFood = {},
        onClickClear = {},
        onClickIgnore = {},
        onClickClearIgnore = {}
    )
}
