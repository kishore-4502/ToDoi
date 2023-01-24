package com.example.todoi.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoi.R
import com.example.todoi.TodoApplication
import com.example.todoi.databinding.FragmentListBinding
import com.example.todoi.models.TodoListAdapter
import com.example.todoi.models.TodoViewModel
import com.example.todoi.models.TodoViewModelFactory
import com.example.todoi.utils.SwipeToDeleteCallBack


class ListFragment : Fragment(),SearchView.OnQueryTextListener {
    private var _binding:FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter:TodoListAdapter

    private val viewModel: TodoViewModel by activityViewModels {
        TodoViewModelFactory(
            (activity?.application as TodoApplication).database.todoDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_item,menu)
        val search = menu.findItem(R.id.search_button)
        val searchView = search.actionView as SearchView
        searchView.isSubmitButtonEnabled = true

        searchView.setOnQueryTextListener(this)
    }
    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query!=null){
            searchDb(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText!=null){
            searchDb(newText)
        }
        return true
    }

    private fun searchDb(text:String){
        val queryString ="%$text%"
        viewModel.searchDatabase(queryString).observe(viewLifecycleOwner){list ->
            list.let {
                adapter.submitList(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TodoListAdapter({
            val action = ListFragmentDirections.actionListFragmentToDetailsFragment(it.id,"Edit Todo","Edit")
            findNavController().navigate(action)
        },
            {
                it.isFinished = !it.isFinished
                viewModel.updateItem(it.id,it.msg,it.date,it.time,it.isFinished,it.priority)
        })
        binding.recyclerView.layoutManager=LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        viewModel.allTodos.observe(viewLifecycleOwner){items ->
            items.let {
                adapter.submitList(it)
            }
        }

        binding.fab.setOnClickListener{
            val action = ListFragmentDirections.actionListFragmentToDetailsFragment(-1,"Add a new Todo","Add")
            findNavController().navigate(action)
        }

        val swipeToDeleteCallBack = object : SwipeToDeleteCallBack(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.layoutPosition
                val item = viewModel.allTodos.value?.get(position)
                viewModel.deleteItem(item!!)
                adapter.notifyItemRemoved(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }
}