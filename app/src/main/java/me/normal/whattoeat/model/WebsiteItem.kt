package me.normal.whattoeat.model

data class WebsiteItem(
    val title: String,
    val subtitle: String,
    val url: String,
    val iconSource: Any // ImageVector 或 Int（drawable 资源 id）
)
