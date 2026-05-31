package me.normal.whattoeat.ui.screens.misc

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
                .width(IntrinsicSize.Max) // 内部控件宽度的最大值
                .fillMaxHeight()
                .padding(paddingValues)
                .padding(top = 30.dp, start = 30.dp, end = 30.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
            val uriHandler = LocalUriHandler.current
            val iconModifer = Modifier.size(36.dp)

            data class Item(val title: String, val subtitle: String, val url: String, val iconSrc: Any)

            val items = listOf(
                Item(
                    "一站式服务大厅",
                    "泥电一站式服务大厅...",
                    "https://ids.xidian.edu.cn/authserver/login?service=https%3A%2F%2Fehall.xidian.edu.cn%3A443%2Fnew%2Findex_xd.html",
                    Icons.Outlined.AccountBalance
                ),
                Item(
                    "电费网站",
                    "Queen提供的电费网站",
                    "http://payment.xidian.edu.cn/MNetWorkUI/showPublic",
                            Icons.Filled.Public
                ),
                Item(
                    "劳动教育查询",
                    "查看劳动教育学时是否已满",
                    "https://xgxt.xidian.edu.cn/xsfw/sys/ldjyappxidian/*default/index.do#/wdjykc",
                    Icons.Filled.Search
                ),
                Item(
                    "四六级报名",
                    "会赢的",
                    "http://cet-bm.neea.edu.cn",
                    Icons.Filled.AddTask
                ),
                Item(
                    "选课",
                    "别睡了，该起床抢课了",
                    "http://xk.xidian.edu.cn",
                    Icons.Filled.Filter
                ),
                Item(
                    "社会实践项目查询",
                    "看看自己社会实践做了几个？",
                    "https://jinshuju.com/f/Yc2cNy/s/tAkU85",
                    Icons.Filled.SavedSearch
                ),
                Item(
                    "Bilibili",
                    "甚至B站",
                    "https://www.bilibili.com/",
                    R.drawable.bilibili
                )
            )

            for(item in items){
                CardButton(
                    title = item.title,
                    subtitle = item.subtitle,
                    modifier = modifier,
                    icon = {
                        if(item.iconSrc is ImageVector){
                            Icon(
                                imageVector = item.iconSrc as ImageVector,
                                contentDescription = null,
                                modifier = iconModifer
                            )
                        } else if(item.iconSrc is Int){
                            Image(
                                painter = painterResource(item.iconSrc as Int),
                                contentDescription = null,
                                modifier = iconModifer
                            )
                        }
                    }
                ){
                    uriHandler.openUri(item.url)
                }
            }
        }
    }
}

@Preview
@Composable
fun PraticalWebsiteScreenPreview(){
    PracticalWebsiteScreen {  }
}