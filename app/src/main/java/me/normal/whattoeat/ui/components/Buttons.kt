package me.normal.whattoeat.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = Modifier
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
fun InfoCardButton(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
){
    Surface(
        onClick = onClick,
        modifier = modifier
    ) { // 需要clickedable
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            icon?.let{
                it()
            }

            Column(){
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

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "点击跳转"
            )
        }
    }
}