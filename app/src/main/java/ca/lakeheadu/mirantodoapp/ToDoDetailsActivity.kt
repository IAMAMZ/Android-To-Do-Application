package ca.lakeheadu.mirantodoapp
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ca.lakeheadu.mirantodoapp.databinding.ActivityToDoDetailsBinding
import java.time.LocalDate
import java.util.Date

class ToDoDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityToDoDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToDoDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val title = intent.getStringExtra("title")
        val isDone = intent.getBooleanExtra("isDone", false)
        val dueDate = intent.getStringExtra("dueDate")

        binding.titleEditText.setText(title);
        binding.statusEditText.setText( if(isDone) "Completed" else "Not Completed")

    }
}