/**
 * Student Name: Miran Qarachatani
 * Student Number: 1197590
 *
 * Description: To do list application UI as part of COMP 3025-G Assignment 3.
 * User navigate between 2 screens a to do list and to do item and can set to due date and add notes to
 * a to do item
 *

 */

package ca.lakeheadu.mirantodoapp
import android.app.AlertDialog
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
import ca.lakeheadu.mirantodoapp.databinding.AddNewTodoItemBinding
import com.google.firebase.FirebaseApp
import java.time.ZoneId


/**
 * This is the app entry point of the application
 */
class MainActivity : AppCompatActivity() {
    // Declare an instance of the binding class
    private lateinit var binding: ActivityMainBinding;

    private lateinit var addNewToDoBinding :AddNewTodoItemBinding;

    // get the view model as a instance member
    private val toDoViewModel: ToDoViewModel by viewModels()


    /**
     *
     * This function runs upon the activity creation
     * it defines some data for the view ( next phase we will get this data from a persistent store)
     *
     * the view model observes changes for each to do item and passes the data and starts the details activity
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this);

        val firestore = FireStoreDataManager();
        firestore.getToDos {
            todos->

                for(todo in todos){
                    println(todo.title);


        }
        }


     /*   val toDos = arrayOf(
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
        )*/


        // call the observe method of the live data of the navigate to data
        toDoViewModel.navigateToDetails.observe(this, Observer { toDoItem ->
            toDoItem?.let {
                val intent = Intent(this, ToDoDetailsActivity::class.java).apply {
                    putExtra("title", it.title)
                    putExtra("isDone", it.isDone)
                    putExtra("notes",it.notes)
                    it.dueDate?.let { date ->

                        putExtra("dueDateMillis", date)
                    }
                }

                startActivity(intent)
                toDoViewModel.onToDoDetailsNavigated()
            }
        })


        // pass the to do items to the to do adapter  and call the callback to pass the data to the view model
//        val toDoAdapter = ToDoAdapter(toDos) {
//            toDoViewModel.onToDoClicked(it)
//        }


        val addToDoFABBtn = binding.addToDoFAB;

        addToDoFABBtn.setOnClickListener{ showToDoModal() }




        //pass the adapter to the recycle view and send the context
//        binding.FirstRecyclerView.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = toDoAdapter
//        }
    }
    private fun showToDoModal()
    {
        val dialogTitle = getString(R.string.add_dialog_title)
        val positiveButtonTitle = getString(R.string.add_todo)
        val builder = AlertDialog.Builder(this)
        addNewToDoBinding = AddNewTodoItemBinding.inflate(layoutInflater)

        builder.setTitle(dialogTitle)
        builder.setView(addNewToDoBinding.root)

        builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
            dialog.dismiss()
            val movieTitle = addNewToDoBinding.movieTitleEditText.text.toString()
            //val newMovie = Movie(title = movieTitle, studio = studioTitle)

            //viewModel.addMovie(newMovie)
        }
        builder.create().show()
    }
}