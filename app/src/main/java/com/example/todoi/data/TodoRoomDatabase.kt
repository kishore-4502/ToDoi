package com.example.todoi.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec

@Database(entities = [Todo::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to =   2),
        AutoMigration(from = 2, to =   3, spec = TodoRoomDatabase.Migration2to3::class),
    ]
    )
abstract class TodoRoomDatabase : RoomDatabase() {

    @DeleteColumn(tableName = "Todo", columnName = "time")
    class Migration2to3:AutoMigrationSpec

    abstract fun todoDao(): TodoDao
    companion object {
        @Volatile
        private var INSTANCE: TodoRoomDatabase? = null

        fun getDatabase(context: Context): TodoRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoRoomDatabase::class.java,
                    "todo_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


}