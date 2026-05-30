package me.normal.whattoeat.data.local.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("food")
data class Food(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // 自动递增, = 0表示未分配，autoGenerate会自动分配唯一id
    @ColumnInfo(name = "time_stamp") val timeStamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "weight") val weight: Int,
    @ColumnInfo(name = "marked") val marked: Boolean
)