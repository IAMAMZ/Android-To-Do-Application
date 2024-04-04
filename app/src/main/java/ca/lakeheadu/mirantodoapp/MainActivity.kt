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
import android.widget.Toast
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

    private lateinit var toDoAdapter: ToDoAdapter


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
        // Initialize the RecyclerView and its adapter here
        initializeRecyclerView()

        // Load to-do items from Firestore
        loadToDosFromFirestore()




        // call the observe method of the live data of the navigate to data
        toDoViewModel.navigateToDetails.observe(this, Observer { toDoItem ->
            toDoItem?.let {
                val intent = Intent(this, ToDoDetailsActivity::class.java).apply {
                    putExtra("title", it.title)
                    putExtra("isDone", it.isDone)
                    putExtra("notes",it.notes)
                    it.dueDate?.let { timestamp  ->

                        putExtra("dueDateMillis", timestamp.toDate().time)
                    }
                }

                startActivity(intent)
                toDoViewModel.onToDoDetailsNavigated()
            }
        })





        val addToDoFABBtn = binding.addToDoFAB;

        addToDoFABBtn.setOnClickListener{ showToDoModal() }





    }
    private fun showToDoModal() {
        val dialogTitle = getString(R.string.add_dialog_title)
        val positiveButtonTitle = getString(R.string.add_todo)
        val builder = AlertDialog.Builder(this)
        addNewToDoBinding = AddNewTodoItemBinding.inflate(layoutInflater)

        builder.setTitle(dialogTitle)
        builder.setView(addNewToDoBinding.root)

        builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
            val toDoTitle = addNewToDoBinding.toDoTitleEditText.text.toString()
            if (toDoTitle.isNotEmpty()) {
                val newToDoItem = ToDoItem(title = toDoTitle, isDone = false)
                saveToDoItem(newToDoItem)
            }
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun initializeRecyclerView() {
        toDoAdapter = ToDoAdapter(arrayOf()) {
            toDoViewModel.onToDoClicked(it)
        }

        binding.FirstRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = toDoAdapter
        }
    }
    private fun updateRecyclerView(toDoItems: Array<ToDoItem>) {
        toDoAdapter = ToDoAdapter(toDoItems) {
            toDoViewModel.onToDoClicked(it)
        }
        binding.FirstRecyclerView.adapter = toDoAdapter
    }


    private fun loadToDosFromFirestore() {
        val firestore = FireStoreDataManager()
        firestore.getToDos { todos ->

            updateRecyclerView(todos.toTypedArray())
        }
    }
    private fun saveToDoItem(toDoItem: ToDoItem) {
        val firestore = FireStoreDataManager()
        firestore.saveToDo(toDoItem) { success ->
            if (success) {
                Toast.makeText(this, "To-Do added successfully", Toast.LENGTH_SHORT).show()
                loadToDosFromFirestore() // Reload the to-do list from Firestore to show the new item
            } else {
                Toast.makeText(this, "Failed to add To-Do", Toast.LENGTH_SHORT).show()
            }
        }
    }



}