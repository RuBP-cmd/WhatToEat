package me.normal.whattoeat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(
    onClickReturn: (() -> Unit),
    title: String? = null,
    onClickMore: (() -> Unit)? = null
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween, // 左边的放最左，右边的放最右，其余均分排布
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(onClickReturn){
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = "返回"
            )
        }

        title?.let{
            Text(
                text = it,
                modifier = Modifier.padding(horizontal = 100.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }

        onClickMore?.let{
            IconButton(it){
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "更多"
                )
            }
        }

    }
}

@Preview
@Composable
private fun TopBarPreview(){
    TopBar({}, "title", {})
}