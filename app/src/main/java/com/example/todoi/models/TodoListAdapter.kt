package com.example.todoi.models
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoi.data.Todo
import com.example.todoi.databinding.TodoStructureBinding



class TodoListAdapter(private var viewModelStoreOwner: ViewModelStoreOwner,
                      private val factory: ViewModelProvider.Factory, private val onItemClicked: (Todo) -> Unit)
    :ListAdapter<Todo,TodoListAdapter.TodoListViewHolder>(DiffCallback){

    class TodoListViewHolder(private var binding:TodoStructureBinding,private val viewModel:TodoViewModel):
    RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item:Todo){
            binding.textView.text = item.msg
            binding.checkbox.isChecked = item.isFinished
            binding.priority.text = "Priority : "+ item.priority
            binding.checkbox.setOnClickListener{
                item.isFinished = !item.isFinished
                viewModel.updateItem(item.id,item.msg,item.date,item.time,item.isFinished,item.priority)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val view = TodoStructureBinding.inflate(LayoutInflater.from(parent.context))
        val viewModel = ViewModelProvider(viewModelStoreOwner, factory).get(TodoViewModel::class.java)
        return TodoListViewHolder(view,viewModel)
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView
            .setOnClickListener {
                onItemClicked(current)
            }
        holder.bind(current)
    }

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.msg == newItem.msg
            }
        }
    }
}