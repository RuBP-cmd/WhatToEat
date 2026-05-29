package me.normal.whattoeat.compose.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.normal.whattoeat.data.local.config.Config
import me.normal.whattoeat.ui.components.ListCard
import me.normal.whattoeat.ui.theme.ColorTheme


@Preview
@Composable
fun SettingsScreen(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        ListCard(
            title = "颜色设置",
            modifier = Modifier.width(300.dp)
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "更换主题",
                        style = MaterialTheme.typography.titleSmall
                    )
                }


                Row(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    val context = LocalContext.current
                    val nowColorTheme by Config.colorThemeFlow(context).collectAsState(ColorTheme.Pink)
                    for(colorTheme in ColorTheme.entries){
                        ColorChooserItem(colorTheme, colorTheme == nowColorTheme)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorChooserItem(colorTheme: ColorTheme, chosen: Boolean){
    val scope = rememberCoroutineScope() // 协程作用域，并且只要组件还再就有，刷新ui不会重复创建
    val context = LocalContext.current
    Surface(
        color = colorTheme.toColorScheme(isSystemInDarkTheme()).primary,
        modifier = (if(chosen) Modifier.border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(6.dp)
        ) else Modifier).padding(3.dp).width(30.dp).height(30.dp),
        shape = RoundedCornerShape(5.dp),
        onClick = {
            scope.launch{ // 启动一个协程
                Config.saveColorTheme(context, colorTheme)
            }
        }

    ) {

    }
}

