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
import java.time.ZoneId

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
            ToDoItem("Wash dishes", false, LocalDate.now().plusDays(3)),
            ToDoItem("Study", true, notes="Review notes on Java and Kotlin"),
            ToDoItem("Exercise", false, LocalDate.now().plusDays(18), "Focus on cardio and strength"),
            ToDoItem("Call family", true, LocalDate.now().plusDays(3), "Remember to ask about the trip"),
            ToDoItem("Grocery shopping", false, LocalDate.now().plusDays(4), "Don't forget the list"),
            ToDoItem("Read a book", false, notes="Start with Atomic Habits"),
            ToDoItem("Fill your taxes", true, LocalDate.now().plusDays(12), "Gather all documents beforehand"),
            ToDoItem("Complete project", true, LocalDate.now().plusDays(14), "Final review with the team"),
            ToDoItem("Clean the house", false, notes="Start with the kitchen and living room"),
            ToDoItem("Plan vacation", false, LocalDate.now().minusDays(13), "Check travel restrictions")
        )



        toDoViewModel.navigateToDetails.observe(this, Observer { toDoItem ->
            toDoItem?.let {
                val intent = Intent(this, ToDoDetailsActivity::class.java).apply {
                    putExtra("title", it.title)
                    putExtra("isDone", it.isDone)
                    putExtra("notes",it.notes)
                    it.dueDate?.let { date ->
                        val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        putExtra("dueDateMillis", millis)
                    }
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