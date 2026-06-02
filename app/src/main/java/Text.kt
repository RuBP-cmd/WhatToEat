import android.provider.CalendarContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle



@Composable
fun BoxText(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    textColor: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current
){
    Box(
        modifier = modifier.background(color = backgroundColor), // 背景颜色
        contentAlignment = Alignment.Center // 默认正中间
    ){
        Text(
            text = text,
            color = textColor,
            style = style
        )
    }
}
