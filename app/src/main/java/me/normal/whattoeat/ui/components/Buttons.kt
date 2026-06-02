package me.normal.whattoeat.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.collections.listOf

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = modifier
            .width(180.dp)
            .height(80.dp)
    ){
        Text(
            text = text,
            fontSize = 30.sp
        )
    }
}

@Composable
fun ElegantButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Surface(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp)),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary,

    ){
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(60.dp)
                .background(brush = gradient),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun AppIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)
){
    IconButton(
        modifier = modifier.defaultMinSize(48.dp, 48.dp),
        onClick = onClick
    ){
        content()
    }
}


@Composable
fun CardButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
){
    Surface(
        onClick = onClick,
        color = Color.Transparent, // 改为透明，方便融入背景
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ){
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ){
            if(icon != null) {
                Box(
                   modifier = Modifier.padding(start = 20.dp)
                ){ icon() }

            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun CardButton(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    isNavigable: Boolean = true,
    onClick: () -> Unit
){
    Surface(
        onClick = onClick,
        color = Color.Transparent, // 改为透明，方便融入背景
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ){
            // Icon
            icon?.let{
                it()
            }

            Box(
                contentAlignment = Alignment.CenterStart
            ){
                Column(){ // 主副标题
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    subtitle?.let{
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // 占满空间

            if(isNavigable){
                Icon( //  >
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "点击跳转",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


