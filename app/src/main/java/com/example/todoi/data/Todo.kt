package com.example.todoi.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoi.utils.Priority


@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id:Int =0,
    val msg:String,
    var isFinished:Boolean,
    var date:String,
    var time:String,
    val priority:Priority,

    @ColumnInfo(name = "details", defaultValue = "Details about the todo")
    val details:String
)