package com.example.todoi.models

import androidx.lifecycle.*
import com.example.todoi.data.Todo
import com.example.todoi.data.TodoDao
import com.example.todoi.utils.Priority
import kotlinx.coroutines.launch

class TodoViewModel(private val todoDao:TodoDao):ViewModel() {

    val allTodos:LiveData<List<Todo>> = todoDao.getItems().asLiveData()

    val date=MutableLiveData("")
    val time=MutableLiveData("")

    fun getTodo(msg:String,date:String,time:String,priority: Priority):Todo{
        return Todo(msg=msg, isFinished = false, date = date, time = time, priority = priority)
    }

    fun addTodo(todo:Todo){
        viewModelScope.launch {
            todoDao.insert(todo)
        }
    }

    fun getItem(id:Int):LiveData<Todo>{
        return todoDao.getItem(id).asLiveData()
    }


    fun updateItem(id:Int,msg:String,date:String,time: String,isFinished:Boolean,priority: Priority){
        val item = Todo(id=id,msg = msg, date = date, time = time, isFinished = isFinished, priority = priority)
        viewModelScope.launch {
            todoDao.update(item)
        }
    }

    fun deleteItem(item:Todo){
        viewModelScope.launch {
            todoDao.delete(item)
        }
    }
    fun searchDatabase(text:String):LiveData<List<Todo>>{
        return todoDao.searchItems(text).asLiveData()
    }

}

class TodoViewModelFactory(private val todoDao: TodoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(todoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}