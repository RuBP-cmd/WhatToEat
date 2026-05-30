package me.normal.whattoeat.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.model.Cell

@Composable
fun ListCard(
    title: String,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)
){
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
    ){
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Box( // 标题
                Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // 内容
            content()
        }
    }

}

@Composable
fun RowItem(
    modifier: Modifier = Modifier,
    cells :List<Cell>
){
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        for(cell in cells) {
            Box(
                modifier = Modifier.weight(cell.weight),
                contentAlignment = Alignment.Center
            ){
                cell.content()
            }
        }
    }
}

