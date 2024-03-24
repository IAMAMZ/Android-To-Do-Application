package ca.lakeheadu.mirantodoapp
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ca.lakeheadu.mirantodoapp.databinding.ActivityToDoDetailsBinding
import java.util.Calendar



/**
 * Activity for displaying and editing the details of a to-do item.
 * Allows users to view details, change due date and edit notes.
 */

class ToDoDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityToDoDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToDoDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extract data from the intent that started this activity
        val title = intent.getStringExtra("title")
        val isDone = intent.getBooleanExtra("isDone", false)
        val dueDateMillis = intent.getLongExtra("dueDateMillis", -1L)



        // Populate the views with the data extracted from the intent.
        binding.titleEditText.setText(title)
        binding.statusTextView.setText(if (isDone) "Completed" else "Not Completed")

        binding.notesEditText.setText(intent.getStringExtra("notes"));
        val today = Calendar.getInstance().timeInMillis
        if (dueDateMillis != -1L) {
            binding.calendarView.date = dueDateMillis
            // can't set up a to do for the past

            // only if the due date in the future if it's in the past then you can reset to past
                if(dueDateMillis>today){
                    binding.calendarView.minDate = today
                }
            }
        else{
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
            finish()
        }

        binding.btnSave.setOnClickListener {

           // Save the to-do item changes when save is clicked. Currently, just closes the activity.inish()

            finish()
        }
    }
}
