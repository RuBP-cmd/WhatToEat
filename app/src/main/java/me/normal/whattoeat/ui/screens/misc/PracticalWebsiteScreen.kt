package me.normal.whattoeat.ui.screens.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.R
import me.normal.whattoeat.ui.components.AppTopBar
import me.normal.whattoeat.ui.components.CardButton

@Composable
fun PracticalWebsiteScreen(
    onReturnToHome: () -> Unit
){
    Scaffold(
        topBar = {
            AppTopBar(
                onClickReturn = onReturnToHome,
                title = "实用网站"
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 50.dp, start = 30.dp, end = 30.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val modifier = Modifier
                .width(350.dp)
                .height(80.dp)
            val uriHandler = LocalUriHandler.current
            val iconModifer = Modifier.size(36.dp)

            CardButton(
                title = "一站式服务大厅",
                subtitle = "泥电一站式服务大厅...",
                modifier = modifier,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalance,
                        contentDescription = "一站式服务大厅",
                        modifier = iconModifer
                    )
                }
            ){
                uriHandler.openUri("https://ids.xidian.edu.cn/authserver/login?service=https%3A%2F%2Fehall.xidian.edu.cn%3A443%2Fnew%2Findex_xd.html")
            }

            CardButton(
                title = "Bilibili",
                subtitle = "甚至B站",
                modifier = modifier,
                icon = {
                    Image(
                        painter = painterResource(R.drawable.bilibili),
                        contentDescription = "Bilibili",
                        modifier = iconModifer
                    )
                }
            ){
                uriHandler.openUri("https://www.bilibili.com/")
            }

            CardButton(
                title = "电费网站",
                subtitle = "Queen提供的电费网站",
                modifier = modifier,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Public,
                        contentDescription = "电费网站",
                        modifier = iconModifer
                    )
                }
            ){
                uriHandler.openUri("http://payment.xidian.edu.cn/MNetWorkUI/showPublic")
            }
        }
    }
}

@Preview
@Composable
fun PraticalWebsiteScreenPreview(){
    PracticalWebsiteScreen {  }
}