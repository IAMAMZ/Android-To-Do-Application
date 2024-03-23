package ca.lakeheadu.mirantodoapp
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ca.lakeheadu.mirantodoapp.databinding.ActivityToDoDetailsBinding
import java.util.Calendar


class ToDoDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityToDoDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToDoDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val isDone = intent.getBooleanExtra("isDone", false)
        val dueDateMillis = intent.getLongExtra("dueDateMillis", -1L)

        binding.titleEditText.setText(title)
        binding.statusEditText.setText(if (isDone) "Completed" else "Not Completed")

        binding.notesEditText.setText(intent.getStringExtra("notes"));

        if (dueDateMillis != -1L) {
            binding.calendarView.date = dueDateMillis
            // can't set up a to do for the past
                val today = Calendar.getInstance().timeInMillis
            // only if the due date in the future if it's in the past then you can reset to past
                if(dueDateMillis>today){
                    binding.calendarView.minDate = today
                }
            }

        setupClickListeners()
    }


    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {

            finish()
        }

        binding.btnDelete.setOnClickListener {
            // delete logic here?
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            // submit logic here?
            finish()
        }
    }
}
