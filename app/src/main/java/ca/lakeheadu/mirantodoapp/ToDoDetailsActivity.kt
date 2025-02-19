package ca.lakeheadu.mirantodoapp

import android.app.Activity
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.lakeheadu.mirantodoapp.databinding.ActivityToDoDetailsBinding
import com.google.protobuf.Timestamp
import java.util.Calendar
import java.util.Date


/**
 * Activity for displaying and editing the details of a to-do item.
 * Allows users to view details, change due date and edit notes.
 */

class ToDoDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityToDoDetailsBinding
    private var id: String? = null
    private var isDone: Boolean? = false
    private var dateSelected: com.google.firebase.Timestamp? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToDoDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extract data from the intent that started this activity
        id = intent.getStringExtra("id")
        val title = intent.getStringExtra("title")
        isDone = intent.getBooleanExtra("isDone", false)
        val dueDateMillis = intent.getLongExtra("dueDateMillis", -1L)



        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val date = selectedDate.time
            Log.i("ToDoDetailsActivity", "Selected Date: $date")
            dateSelected = com.google.firebase.Timestamp(date)

        }
        // Populate the views with the data extracted from the intent.
        binding.titleEditText.setText(title)
        binding.statusTextView.setText(if (isDone as Boolean) "Completed" else "Not Completed")

        binding.notesEditText.setText(intent.getStringExtra("notes"));
        val today = Calendar.getInstance().timeInMillis
        if (dueDateMillis != -1L) {
            binding.calendarView.date = dueDateMillis
            // can't set up a to do for the past

            // only if the due date in the future if it's in the past then you can reset to past
            if (dueDateMillis > today) {
                binding.calendarView.minDate = today
            }
        } else {
            // if due date not set still limit
            binding.calendarView.minDate = today
        }

        setupClickListeners()
    }


    /**
     * Setup click listeners for the save, cancel, and delete buttons.
     */
    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            // Close the activity without saving when cancel is clicked.
            finish()
        }

        binding.btnDelete.setOnClickListener {
            // Implement delete logic here. Currently, just closes the activity.
            id?.let { toDoId ->
                val firestore = FireStoreDataManager()
                firestore.deleteToDo(id!!) { success ->
                    if (success) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        // Update failed, show an error message
                        Toast.makeText(this, "Failed to delete to-do item", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        binding.btnSave.setOnClickListener {
            // Get the updated values from the UI
            val updatedTitle = binding.titleEditText.text.toString()
            val updatedNotes = binding.notesEditText.text.toString()

            val updatedDueDate = dateSelected;


            // Update the to-do item in Firestore
            id?.let { toDoId ->
                val firestore = FireStoreDataManager()
                if (updatedDueDate != null) {
                    firestore.updateToDoItem(
                        toDoId,
                        updatedTitle,
                        updatedNotes,
                        updatedDueDate
                    ) { success ->
                        if (success) {
                            //val updatedToDoItem = ToDoItem(toDoId, updatedTitle, isDone, updatedDueDate, updatedNotes)
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            // Update failed, show an error message
                            Toast.makeText(this, "Failed to update to-do item", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    firestore.updateToDoItem(toDoId, updatedTitle, updatedNotes, null) { success ->
                        if (success) {
                            //val updatedToDoItem = ToDoItem(toDoId, updatedTitle, isDone, updatedDueDate, updatedNotes)
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            // Update failed, show an error message
                            Toast.makeText(this, "Failed to update to-do item", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

    }
}
