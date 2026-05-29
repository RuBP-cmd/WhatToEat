package me.normal.whattoeat.compose.misc

import android.icu.number.Scale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.ui.components.TopBar


@Composable
fun EatScreen(
    onNavigateToFoodEdit: () -> Unit,
    onReturnToHome: () -> Unit
){
    Scaffold(
        topBar = { TopBar(onReturnToHome, "Eat", onNavigateToFoodEdit) }
    ){ paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ){
            Text(
                text = "EatScreen, 这里还什么都没有哦~",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(100.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

}

@Preview
@Composable
private fun EatScreenPreview(){
    EatScreen(
        {},
        {}
    )
}