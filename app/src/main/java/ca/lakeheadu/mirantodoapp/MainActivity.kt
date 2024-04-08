/**
 * Student Name: Miran Qarachatani
 * Student Number: 1197590
 *
 * Description: To do list application UI as part of COMP 3025-G Assignment 4
 * This part I extended the UI of to do application  done in assignment 3 and
 * added persistent data and full functionality
 *

 */

package ca.lakeheadu.mirantodoapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import ca.lakeheadu.mirantodoapp.databinding.ActivityMainBinding
import java.time.LocalDate
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ca.lakeheadu.mirantodoapp.databinding.AddNewTodoItemBinding
import com.google.firebase.FirebaseApp
import java.time.ZoneId


/**
 * This is the app entry point of the application
 */
class MainActivity : AppCompatActivity() {
    // Declare an instance of the binding class
    private lateinit var binding: ActivityMainBinding;

    private lateinit var addNewToDoBinding: AddNewTodoItemBinding;

    private lateinit var toDoAdapter: ToDoAdapter

    // get the view model as a instance member
    private val toDoViewModel: ToDoViewModel by viewModels()

    // list of the to do items in our main activity
    private lateinit var toDoItemsList: MutableList<ToDoItem>


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

        // initialize the recycle view
        initializeRecyclerView()

        // get the to dos from the view model
        toDoViewModel.getAllToDos()

        // set up swipe and add the swipe to delete call back to it
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)

        // attach the swipe helper to the recycle view
        itemTouchHelper.attachToRecyclerView(binding.FirstRecyclerView)

        // call the observe method of the live data of the navigate to data
        toDoViewModel.navigateToDetails.observe(this, Observer { toDoItem ->
            toDoItem?.let {
                // pass the data through intent
                val intent = Intent(this, ToDoDetailsActivity::class.java).apply {
                    putExtra("id", it.id)
                    putExtra("title", it.title)
                    putExtra("isDone", it.isDone)
                    putExtra("notes", it.notes)
                    it.dueDate?.let { timestamp ->
                        putExtra("dueDateMillis", timestamp.toDate().time)
                    }
                }
                // start the to do details activity
                startForResult.launch(intent)
                // if item saved or deleted notify the view model
                toDoViewModel.onToDoDetailsNavigated()
            }
        })

        // Set up the click listener for the add to-do FAB button
        val addToDoFABBtn = binding.addToDoFAB;
        addToDoFABBtn.setOnClickListener { showToDoModal() }
    }

    // Callback for TodoDetails activity to refresh the to do items in case of update
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                toDoViewModel.getAllToDos()
            }
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

    // observe changes in toDoItems live data, and instantiate the adapter and add it to recycle view
    private fun initializeRecyclerView() {
        toDoViewModel.toDos.observe(this) { toDoItems ->
            toDoItemsList = toDoItems.toMutableList()
            toDoAdapter = ToDoAdapter(toDoItems) { toDoViewModel.onToDoClicked(it) }

            binding.FirstRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = toDoAdapter
            }
        }
    }


    private fun saveToDoItem(toDoItem: ToDoItem) {
        val firestore = FireStoreDataManager()
        firestore.saveToDo(toDoItem) { success ->
            if (success) {
                Toast.makeText(this, "To-Do added successfully", Toast.LENGTH_SHORT).show()
                toDoViewModel.getAllToDos()
            } else {
                Toast.makeText(this, "Failed to add To-Do", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private val swipeToDeleteCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Move operations are not supported
                return false
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val toDoItem = toDoAdapter.getItemById(position)

                // Create and display an AlertDialog for confirmation
                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle("Delete ToDo")
                    setMessage("Are you sure you want to delete this to-do item?")
                    setPositiveButton("Yes") { dialog, which ->
                        // delte  if user confirms
                        toDoItem?.id?.let { toDoId ->
                            FireStoreDataManager().deleteToDo(toDoId) { success ->
                                if (success) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Item deleted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // refresh the to do items
                                    toDoViewModel.getAllToDos()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Failed to delete item",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    setNegativeButton("No") { dialog, which ->
                        //dismiss the dialog if user says no
                        toDoAdapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }
                    setCancelable(false)
                    show()
                }
            }

        }


}