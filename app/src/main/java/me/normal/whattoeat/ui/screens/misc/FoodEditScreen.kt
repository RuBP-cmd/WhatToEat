package me.normal.whattoeat.ui.screens.misc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        onClickStar = { food -> foodViewModel.update(food.copy(marked = !food.marked)) },
        onInputName = { food, name -> foodViewModel.update(food.copy(name = name)) },
        onInputWeight = { food, weight -> foodViewModel.update(food.copy(weight = weight)) }
    )
}


@Composable
fun FoodEditContent(
    foodList: List<Food>,
    onReturnToEat: () -> Unit,
    onClickStar: (food: Food) -> Unit,
    onInputName: (food: Food, name: String) -> Unit,
    onInputWeight: (food: Food, weight: Int) -> Unit
){
    Scaffold(
        topBar = { TopBar(onReturnToEat, "编辑清单") }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
            contentAlignment = Alignment.Center
        ){
            val weightList = listOf(1f, 4f, 2f)
            val titleStyle = MaterialTheme.typography.titleMedium
            val textStyle = MaterialTheme.typography.bodyMedium
            LazyColumn(

            ) {
                item{
                    RowItem(listOf(
                        Cell({ Text(text = "", style = titleStyle) }, weightList[0]),
                        Cell({ Text(text = "", style = titleStyle) }, weightList[1]),
                        Cell({ Text(text = "", style = titleStyle) }, weightList[2])
                    ))
                }

                items(foodList){ food ->
                    RowItem(listOf(
                        Cell(
                            content = {
                                AppIconButton({ onClickStar(food) }) {
                                    Icon(
                                        imageVector = if(food.marked) Icons.Filled.Star else Icons.Outlined.Star,
                                        contentDescription = null
                                    )
                                }
                            },
                            weight = weightList[0]
                        ),
                        Cell(
                            content = { TextField(
                                value = food.name,
                                textStyle = textStyle,
                                onValueChange = { name -> onInputName(food, name) }
                            ) },
                            weight = weightList[1]
                        ),
                        Cell(
                            content = { TextField(
                                value = food.weight.toString(),
                                textStyle = textStyle,
                                onValueChange = {}
                            ) },
                            weight = weightList[2]
                        )

                    ))

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
        { food, name -> } ,
        {food, weight -> }
    )
}

