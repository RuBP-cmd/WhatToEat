package me.normal.whattoeat.compose.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.ui.components.ElegantButton
import me.normal.whattoeat.ui.components.TopBar


@Composable
fun EatScreen(
    onNavigateToFoodEdit: () -> Unit,
    onReturnToHome: () -> Unit
){
    Scaffold(
        topBar = { TopBar(onReturnToHome, "Eat", onNavigateToFoodEdit) }
    ){ paddingValues ->

        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ){
            Card(
                modifier = Modifier.width(250.dp).height(70.dp),
            ){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "点击查询今天吃什么",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

            }

            ElegantButton("点击查询"){}

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