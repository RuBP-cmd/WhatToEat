package me.normal.whattoeat.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.normal.whattoeat.ui.components.CardButton


@Composable
fun HomeScreen(
    onNavigateToEat: () -> Unit,
    onNavigateToPracticalWebsite: () -> Unit,
    onNavigateToOther: () -> Unit
){

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ){
        Spacer(modifier = Modifier.padding(30.dp))
        MobaMessageCard()

        Column(
            modifier = Modifier.width(IntrinsicSize.Max),
//            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            val modifier = Modifier.height(80.dp)
            CardButton(
                title = "Moba",
                subtitle = "我们需要开始moba！",
                modifier = modifier
            ){ onNavigateToEat() }
            CardButton(
                title = "实用网站",
                subtitle = "看看有哪些实用网站",
                modifier = modifier
            ){ onNavigateToPracticalWebsite() }
            CardButton(
                title = "其他",
                modifier = modifier,
            ){ onNavigateToOther() }
        }
    }

}

@Composable
private fun MobaMessageCard(){
    Card(
        modifier = Modifier
            .padding(30.dp)
            .width(400.dp)
            .height(80.dp)
    ){
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "点击Moba开始mobaQueen!",
                modifier = Modifier.padding(10.dp),
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview(){
    HomeScreen(
        {},
        {},
        {}
    )
}