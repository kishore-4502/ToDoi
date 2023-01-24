package com.example.todoi.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoi.R
import com.example.todoi.TodoApplication
import com.example.todoi.data.Todo
import com.example.todoi.databinding.FragmentDetailsBinding
import com.example.todoi.models.TodoViewModel
import com.example.todoi.models.TodoViewModelFactory
import com.example.todoi.utils.Priority

class DetailsFragment : Fragment() {
    private var _binding:FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val navigationArgs:DetailsFragmentArgs by navArgs()
    lateinit var item: Todo
    private val viewModel: TodoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id
        val buttonName = navigationArgs.buttonName


        //For DropDown Menu
        val priorityList = resources.getStringArray(R.array.Priority)
        val dropDownAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,priorityList)
        binding.priorityVal.setAdapter(dropDownAdapter)

        val calendar =Calendar.getInstance()

        //If id>0 --> Edit page
        if(id > 0){
            binding.dateButton.setOnClickListener {
                val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                    val actualMonth = month+1
                    item.date="$year-$actualMonth-$dayOfMonth"
                    binding.dateShow.text = item.date
                },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.datePicker.minDate = calendar.timeInMillis
                datePicker.show()
            }
            binding.timeButton.setOnClickListener {
                val timePickerDialog = TimePickerDialog(requireContext(),
                    { _, hour, min ->
                        if(hour<10 && min<10){
                            item.time = "0$hour-0$min"
                        }
                        else if(hour<10){
                            item.time = "0$hour-$min"
                        }
                        else if(min<10){
                            item.time = "$hour-0$min"
                        }else{
                            item.time = "$hour-$min"
                        }
                        binding.timeShow.text = item.time
                    },
                    calendar.get(Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            }
            binding.addButton.text = buttonName
            binding.deleteButton.visibility = View.VISIBLE
            binding.addToCalendarButton.visibility = View.VISIBLE
            viewModel.getItem(id).observe(this.viewLifecycleOwner){
                item = it
                binding.apply {
                    inpMsg.setText(item.msg,TextView.BufferType.SPANNABLE)
                    inpDetails.setText(item.details,TextView.BufferType.SPANNABLE)
                    dateShow.setText(item.date, TextView.BufferType.SPANNABLE)
                    timeShow.setText(item.time, TextView.BufferType.SPANNABLE)

                    addButton.setOnClickListener {
                        val msg = binding.inpMsg.text.toString()
                        val details = binding.inpDetails.text.toString()
                        val priorityText=binding.priorityVal.text.toString()
                        Log.d("TAG",priorityText)
                        val priority:Priority = when(priorityText){
                            "High" ->Priority.HIGH
                            "Medium" ->Priority.MEDIUM
                            else -> Priority.LOW
                        }
                        if(msg!="" && item.date!="Pick a Date" && item.time!="Pick a Time" && details!=""){
                            if(priorityText=="Priority"){
                                viewModel.updateItem(navigationArgs.id,msg,item.date,item.time,item.isFinished,item.priority,details)
                            }else{
                                viewModel.updateItem(navigationArgs.id,msg,item.date,item.time,item.isFinished,priority,details)
                            }
                            findNavController().navigateUp()
                        }
                    }
                    deleteButton.setOnClickListener {
                        viewModel.deleteItem(item)
                        findNavController().navigateUp()
                    }
                    addToCalendarButton.setOnClickListener {
                        val dateString = binding.dateShow.text.toString()
                        val timeString = binding.timeShow.text.toString()
                        val dateList = dateString.split("-")
                        val timeList = timeString.split("-")
                        val year = dateList[0].toIntOrNull()
                        val month = ((dateList[1]).toIntOrNull())?.minus(1)
                        val date = dateList[2].toIntOrNull()
                        val hour = timeList[0].toIntOrNull()
                        val min = timeList[1].toIntOrNull()

                        if(year!=null && month!=null && date!=null && hour!=null && min!=null){
                            val beginTime = Calendar.getInstance()
                            beginTime.set(year, month, date, hour, min)
                            val startMillis = beginTime.timeInMillis

                            val insertCalendarIntent = Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events.TITLE,binding.inpMsg.text.toString() )
                                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,startMillis)
                                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,startMillis)

                            startActivity(insertCalendarIntent)
                        }else{
                            Toast.makeText(requireContext(),"Invalid date",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        //else --> Add page
        else{
            viewModel.date.observe(viewLifecycleOwner){
                binding.dateShow.text = it
            }
            viewModel.time.observe(viewLifecycleOwner){
                binding.timeShow.text = it
            }
            binding.dateButton.setOnClickListener {
                val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                    val actualMonth = month+1
                    viewModel.date.value = "$year-$actualMonth-$dayOfMonth"
                },
                    calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.datePicker.minDate = calendar.timeInMillis
                datePicker.show()
            }
            binding.timeButton.setOnClickListener {
                val timePickerDialog = TimePickerDialog(requireContext(),
                    { _, hour, min ->
                        if(hour<10 && min<10){
                            viewModel.time.value = "0$hour-0$min"
                        }
                        else if(hour<10){
                            viewModel.time.value = "0$hour-$min"
                        }
                        else if(min<10){
                            viewModel.time.value = "$hour-0$min"
                        }else{
                            viewModel.time.value = "$hour-$min"
                        }
                    },
                    calendar.get(Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            }
            binding.addButton.setOnClickListener{
                val msg = binding.inpMsg.text.toString()
                val details = binding.inpDetails.text.toString()
                val priorityText=binding.priorityVal.text.toString()
                val priority:Priority = when(priorityText){
                    "High" ->Priority.HIGH
                    "Medium" ->Priority.MEDIUM
                    else -> Priority.LOW
                }
                if(msg!="" && viewModel.date.value!="Pick a Date"
                    && viewModel.time.value!="Pick a Time"
                    && priorityText!="Priority"
                    && details!=""
                   )
                {
                    viewModel.addTodo(viewModel.getTodo(msg,viewModel.date.value!!,viewModel.time.value!!,priority,details))
                    viewModel.resetDateAndTime()
                    findNavController().navigateUp()
                }
                else{
                    Toast.makeText(requireContext(),"Fill all fields",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
