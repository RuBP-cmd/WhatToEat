package me.normal.whattoeat.ui.screens.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.normal.whattoeat.R
import me.normal.whattoeat.data.local.entry.Food
import me.normal.whattoeat.model.Cell
import me.normal.whattoeat.ui.components.AppIconButton
import me.normal.whattoeat.ui.components.AppTopBar
import me.normal.whattoeat.ui.components.RowItem
import me.normal.whattoeat.ui.viewmodel.FoodViewModel

@Composable
fun FoodEditScreen(
    foodViewModel: FoodViewModel,
    onReturnToEat: () -> Unit
){
    val foodList by foodViewModel.foods.collectAsState(initial = emptyList()) // collect
    FoodEditContent (
        foodList,
        onReturnToEat = onReturnToEat,
        onClickAddRow = { foodViewModel.insert(Food(name = "", weight = 1, marked = true)) },
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
    var isDeleteMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppTopBar(onReturnToEat, "编辑清单"){ isChooseBarShown = !isChooseBarShown} }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
//                .clickable( // 点击外部区域可以取消
//                    enabled = true,
//                    interactionSource = remember { MutableInteractionSource() }, // 禁用点击波纹
//                    indication = null
//                ) {
//                    isChooseBarShown = false
//                    isDeleteMode = false
//                },
            contentAlignment = Alignment.Center
        ){
            // 编辑表
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 30.dp, end = 30.dp, top = 100.dp, bottom = 80.dp),
                shape = RoundedCornerShape(12.dp)
            ){
                EditTable(
                    foodList = foodList,
                    isDeleteMode = isDeleteMode,
                    onClickStar = onClickStar,
                    onInputName = onInputName,
                    onInputWeight = onInputWeight,
                    onClickDelRow = onClickDelRow,
                )
            }

            // 弹出栏
            if(isChooseBarShown){
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-1).dp, y = 3.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(18.dp)
                        )
                ){
                    AppIconButton( onClickAddRow ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircleOutline,
                            contentDescription = "添加行"
                        )
                    }
                    AppIconButton( { isDeleteMode = !isDeleteMode } ) {
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

@Composable
private fun EditTable(
    foodList: List<Food>,
    isDeleteMode: Boolean,
    onClickStar: (food: Food) -> Unit,
    onInputName: (food: Food, name: String) -> Unit,
    onInputWeight: (food: Food, weight: Int) -> Unit,
    onClickDelRow: (food: Food) -> Unit
){
    val weightList = listOf(2f, 8f, 3f)
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
        modifier = Modifier
            .fillMaxSize()
            .background(color = bodyColor),
    ) {
        stickyHeader{ // 表头
            RowItem(
                modifier = Modifier
                    .background(headColor)
                    .padding(horizontal = horizontalPaddingValues),
                cells = listOf(
                    Cell({ Text(text = "参选", color = titleColor, style = titleStyle) }, weightList[0]),
                    Cell({ Text(text = "名称", color = titleColor, style = titleStyle) }, weightList[1]),
                    Cell({ Text(text = "权重", color = titleColor, style = titleStyle) }, weightList[2])
                ))
        }

        // 表内容
        items(foodList){ food ->
            var isError by remember { mutableStateOf(false) }
            var foodWeight = food.weight.toString()

            Box(
                modifier = Modifier.fillMaxWidth()
            ){
                RowItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(horizontal = horizontalPaddingValues),
                    cells = listOf(
                        Cell(
                            content = {
                                AppIconButton({ onClickStar(food) }) {
                                    Image(
                                        painter = painterResource(if(food.marked) R.drawable.filled_star else R.drawable.outlined_star),
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
                                placeholder = { Text(text = "请输入名称", color = MaterialTheme.colorScheme.onSurfaceVariant) }, // 输入为空的提示信息
                                onValueChange = { name -> onInputName(food, name) }
                            ) },
                            weight = weightList[1]
                        ),
                        Cell(
                            content = { TextField(
                                value = foodWeight,
                                textStyle = textStyle,
                                colors = textFieldColors,
                                onValueChange = { newFoodWeight ->
                                    if(newFoodWeight.length in 1..5 && newFoodWeight.all{ it.isDigit()}){
                                        onInputWeight(food, newFoodWeight.toInt())
                                        isError = false
                                    } else isError = true
                                },
                                isError = isError,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // 只允许输入数字
                            ) },
                            weight = weightList[2]
                        )
                    )
                ) // 结束
                ////////

                if(isDeleteMode){
                    AppIconButton(
                        { onClickDelRow(food) },
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-2).dp, y = 2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = CircleShape
                            )
//                            .shadow(
//                                elevation = 8.dp,
//                                shape = CircleShape,
//                                clip = true,
//                            )
                    ){
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "删除行"
                        )
                    }
                }

            }

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

