package me.normal.whattoeat.compose.misc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.ui.components.TopBar


@Composable
fun FoodEditScreen(
    onReturnToEat: () -> Unit
){
    Scaffold(
        topBar = { TopBar(onReturnToEat, "编辑清单") }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ){
            Text(
                "FoodEditScreen, 这里还什么都没有哦~",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(100.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }

    }
}

