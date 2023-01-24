package com.example.todoi.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * from todo")
    fun getItems(): Flow<MutableList<Todo>>


    @Query("SELECT * from todo WHERE msg LIKE :text")
    fun searchItems(text:String): Flow<List<Todo>>

    @Query("SELECT * from todo WHERE id = :id")
    fun getItem(id: Int): Flow<Todo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Todo)

    @Update
    suspend fun update(item: Todo)

    @Delete
    suspend fun delete(item: Todo)
}