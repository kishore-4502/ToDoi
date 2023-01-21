package com.example.todoi

import android.app.Application
import com.example.todoi.data.TodoRoomDatabase

class TodoApplication:Application() {

    val database : TodoRoomDatabase by lazy {
        TodoRoomDatabase.getDatabase(this)
    }
}