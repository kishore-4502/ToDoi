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



class TodoListAdapter(private val onItemClicked: (Todo) -> Unit,private val onCheckBoxClicked:(Todo)->Unit)
    :ListAdapter<Todo,TodoListAdapter.TodoListViewHolder>(DiffCallback){

    class TodoListViewHolder(private var binding:TodoStructureBinding):
    RecyclerView.ViewHolder(binding.root)
    {
        val checkBox = binding.checkbox
        fun bind(item:Todo){
            binding.textView.text = item.msg
            binding.checkbox.isChecked = item.isFinished
            binding.priority.text = "Priority : "+ item.priority
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val view = TodoStructureBinding.inflate(LayoutInflater.from(parent.context))
        return TodoListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView
            .setOnClickListener {
                onItemClicked(current)
            }
        holder.checkBox.setOnClickListener {
            onCheckBoxClicked(current)
        }
        holder.bind(current)
    }

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem == newItem
            }
        }
    }
}