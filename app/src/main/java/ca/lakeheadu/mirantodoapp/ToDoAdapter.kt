package ca.lakeheadu.mirantodoapp
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import ca.lakeheadu.mirantodoapp.databinding.ToDoRowBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ToDoAdapter(private val dataSet: Array<ToDoItem>, private val onItemClicked: (ToDoItem) -> Unit) :
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>()
{

    class ViewHolder(val binding: ToDoRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder
    {

        val binding = ToDoRowBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener {
            onItemClicked(dataSet[position])
        }
        viewHolder.binding.todoText.text = dataSet[position].title
        val dueDate = dataSet[position].dueDate
        viewHolder.binding.todoSwitch.isChecked = dataSet[position].isDone

        if (dueDate != null) {
            viewHolder.binding.todoDueDate.text = dueDate.toString()

            // Calculate the number of days until the due date
            val daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), dueDate)
            viewHolder.binding.todoDueDate.setTextColor(
                when {
                    daysUntilDue < 0 -> Color.parseColor("#7f0205") // Overdue
                    daysUntilDue <= 7 -> Color.parseColor("#FF5722")// Due within a week
                    else -> Color.parseColor("#689F38")// More than a week away
                }
            )
        } else {
            viewHolder.binding.todoDueDate.visibility = View.GONE
        }
    }

    override fun getItemCount() = dataSet.size
}