package me.normal.whattoeat.ui.screens.food

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.data.local.entry.FoodTable
import kotlin.collections.forEach

// --- 可编辑书签侧栏 ---
// 长按弹出菜单，可选择重命名、删除

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditBookmarkSidebar(
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
            val isChosen = table.id == currentTableId // 目前被选中的表

            Box {
                Surface(
                    modifier = Modifier
                        .width(56.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                        .combinedClickable(
                            onClick = { onTableSelected(table.id) },
                            onLongClick = { contextMenuTableId = table.id }
                        ),
                    shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp),
                    color = if (isChosen) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = table.name,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isChosen) MaterialTheme.colorScheme.onPrimary
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
        IconButton(
            onClick = onAddTable,
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "新建表格",
                modifier = Modifier.size(24.dp)
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
                TextButton(onClick = { showRenameDialog = false }) {
                    Text(text = "取消", color = MaterialTheme.colorScheme.onSurface)
                }
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
                TextButton(onClick = { showDeleteDialog = false }) { Text(text = "取消", color = MaterialTheme.colorScheme.onSurface) }
            }
        )
    }
}