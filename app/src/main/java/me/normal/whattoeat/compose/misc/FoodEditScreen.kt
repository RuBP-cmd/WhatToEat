package me.normal.whattoeat.compose.misc

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun FoodEditScreen(){
    Text(
        "FoodEditScreen, 这里还什么都没有哦~",
        modifier = Modifier.fillMaxSize().padding(100.dp),
        style = MaterialTheme.typography.titleLarge)
}