package ca.lakeheadu.mirantodoapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import ca.lakeheadu.mirantodoapp.databinding.ActivityMainBinding
import java.time.LocalDate
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    // Declare an instance of the binding class
    private lateinit var binding: ActivityMainBinding;
    private val toDoViewModel: ToDoViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toDos = arrayOf(
            ToDoItem("Wash dishes", false, LocalDate.now()),
            ToDoItem("Study",false),
            ToDoItem("Exercise",false),
            ToDoItem("anything",true),
            ToDoItem("item3",false)
        )

        toDoViewModel.navigateToDetails.observe(this, Observer { toDoItem ->
            toDoItem?.let {
                val intent = Intent(this, ToDoDetailsActivity::class.java).apply {
                    putExtra("title", it.title)
                    putExtra("isDone", it.isDone)
                    it.dueDate?.let { date -> putExtra("dueDate", date.toString()) } // maybe pass it as date idk
                }
                startActivity(intent)
                toDoViewModel.onToDoDetailsNavigated()
            }
        })


        val toDoAdapter = ToDoAdapter(toDos) {
            toDoViewModel.onToDoClicked(it)
        }
        // Use view binding to replace findViewById or synthetic properties
        binding.FirstRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = toDoAdapter
        }
    }
}