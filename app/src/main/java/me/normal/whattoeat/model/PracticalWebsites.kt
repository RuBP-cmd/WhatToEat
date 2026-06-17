package me.normal.whattoeat.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountBalance
import me.normal.whattoeat.R

val practicalWebsites = listOf(
    WebsiteItem(
        title = "一站式服务大厅",
        subtitle = "泥电一站式服务大厅...",
        url = "https://ehall.xidian.edu.cn/",
        iconSource = Icons.Outlined.AccountBalance
    ),
    WebsiteItem(
        title = "电费网站",
        subtitle = "Queen提供的电费网站",
        url = "https://ignypt.xidian.edu.cn/revenueH5/mainPage",
        iconSource = Icons.Filled.Public
    ),
    WebsiteItem(
        title = "劳动教育查询",
        subtitle = "查看劳动教育学时是否已满",
        url = "https://xgxt.xidian.edu.cn/xsfw/sys/ldjyappxidian/*default/index.do#/wdjykc",
        iconSource = Icons.Filled.Search
    ),
    WebsiteItem(
        title = "四六级",
        subtitle = "会赢的",
        url = "http://cet-bm.neea.edu.cn",
        iconSource = Icons.Filled.AddTask
    ),
    WebsiteItem(
        title = "选课",
        subtitle = "别睡了，该起床抢课了",
        url = "http://xk.xidian.edu.cn",
        iconSource = Icons.Filled.Filter
    ),
    WebsiteItem(
        title = "社会实践项目查询",
        subtitle = "看看自己社会实践做了几个？",
        url = "https://jinshuju.com/f/Yc2cNy/s/tAkU85",
        iconSource = Icons.Filled.SavedSearch
    ),
    WebsiteItem(
        title = "Bilibili",
        subtitle = "甚至B站",
        url = "https://www.bilibili.com/",
        iconSource = R.drawable.bilibili
    ),
    WebsiteItem(
        title = "IP纯净度测试",
        subtitle = "测测你的IP是否干净",
        url = "https://ping0.cc/",
        iconSource = Icons.Filled.NetworkCell
    )
)
