package me.normal.whattoeat.ui.screens.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.data.local.entry.Food
import me.normal.whattoeat.model.Cell
import me.normal.whattoeat.ui.components.AppIconButton
import me.normal.whattoeat.ui.components.RowItem
import me.normal.whattoeat.ui.components.TopBar
import me.normal.whattoeat.ui.viewmodel.FoodViewModel

@Composable
fun FoodEditScreen(
    foodViewModel: FoodViewModel,
    onReturnToEat: () -> Unit
){
    val foodList by foodViewModel.foods.collectAsState(initial = emptyList())
    FoodEditContent (
        foodList,
        onReturnToEat = onReturnToEat,
        onClickAddRow = { foodViewModel.insert(Food(name = "请输入名字", weight = 1, marked = true)) },
        onClickDelRow = { food -> foodViewModel.delete(food) },
        onClickStar = { food -> foodViewModel.update(food.copy(marked = !food.marked)) },
        onInputName = { food, name -> foodViewModel.update(food.copy(name = name)) },
        onInputWeight = { food, weight -> foodViewModel.update(food.copy(weight = weight)) }
    )
}


@Composable
fun FoodEditContent(
    foodList: List<Food>,
    onReturnToEat: () -> Unit,
    onClickAddRow: () -> Unit,
    onClickDelRow: (food: Food) -> Unit,
    onClickStar: (food: Food) -> Unit,
    onInputName: (food: Food, name: String) -> Unit,
    onInputWeight: (food: Food, weight: Int) -> Unit
){
    var isChooseBarShown by remember { mutableStateOf(false)}

    Scaffold(
        topBar = { TopBar(onReturnToEat, "编辑清单"){ isChooseBarShown = !isChooseBarShown} }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
            contentAlignment = Alignment.Center
        ){

            Box(
                modifier = Modifier.fillMaxSize()
            ){
                // 编辑表
                Surface(
                    modifier = Modifier.fillMaxSize().padding(start = 30.dp, end = 30.dp, top = 100.dp, bottom = 80.dp),
                    shape = RoundedCornerShape(12.dp)
                ){
                    EditTable(
                        foodList = foodList,
                        onClickStar = onClickStar,
                        onInputName = onInputName,
                        onInputWeight = onInputWeight
                    )
                }

                // 弹出栏
                if(isChooseBarShown){
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-1).dp, y = 3.dp)
                    ){
                        AppIconButton( onClickAddRow ) {
                            Icon(
                                imageVector = Icons.Filled.AddCircleOutline,
                                contentDescription = "添加行"
                            )
                        }
                        AppIconButton( {} ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "编辑"
                            )
                        }
                    }
                }

            }
        }

    }
}

@Composable
private fun EditTable(
    foodList: List<Food>,
    onClickStar: (food: Food) -> Unit,
    onInputName: (food: Food, name: String) -> Unit,
    onInputWeight: (food: Food, weight: Int) -> Unit
){
    val weightList = listOf(1f, 4f, 2f)
    val headColor = MaterialTheme.colorScheme.primary
    val titleColor = MaterialTheme.colorScheme.onPrimary
    val bodyColor = MaterialTheme.colorScheme.secondary
    val titleStyle = MaterialTheme.typography.titleMedium
    val textStyle = MaterialTheme.typography.bodyMedium
    val horizontalPaddingValues = 5.dp
    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(color = bodyColor),
    ) {
        item{
            RowItem(
                modifier = Modifier.background(headColor).padding(horizontal = horizontalPaddingValues),
                cells = listOf(
                    Cell({ Text(text = "参选", color = titleColor, style = titleStyle) }, weightList[0]),
                    Cell({ Text(text = "名称", color = titleColor, style = titleStyle) }, weightList[1]),
                    Cell({ Text(text = "权重", color = titleColor, style = titleStyle) }, weightList[2])
                ))
        }

        items(foodList){ food ->
            var isError by remember { mutableStateOf(false) }
            RowItem(
                modifier = Modifier.padding(horizontal = horizontalPaddingValues),
                cells = listOf(
                    Cell(
                        content = {
                            AppIconButton({ onClickStar(food) }) {
                                Icon(
                                    imageVector = if(food.marked) Icons.Filled.Star else Icons.Outlined.Home,
                                    contentDescription = "参选"
                                )
                            }
                        },
                        weight = weightList[0]
                    ),
                    Cell(
                        content = { TextField(
                            value = food.name,
                            textStyle = textStyle,
                            colors = textFieldColors,
                            onValueChange = { name -> onInputName(food, name) }
                        ) },
                        weight = weightList[1]
                    ),
                    Cell(
                        content = { TextField(
                            value = food.weight.toString(),
                            textStyle = textStyle,
                            colors = textFieldColors,
                            onValueChange = { weightStr ->
                                if(weightStr.length in 1..10){
                                    isError = false
                                    onInputWeight(food, weightStr.toInt())
                                } else isError = true
                            },
                            isError = isError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // 只允许输入数字
                        ) },
                        weight = weightList[2]
                    )
                ))
        }


    }
}


@Preview
@Composable
fun FoodEditContentPreview(){
    FoodEditContent(
        emptyList(),
        {},
        {},
        {},
        {},
        { food, name -> } ,
        {food, weight -> },
    )
}

