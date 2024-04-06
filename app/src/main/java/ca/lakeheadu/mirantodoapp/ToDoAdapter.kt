package ca.lakeheadu.mirantodoapp
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import ca.lakeheadu.mirantodoapp.databinding.ToDoRowBinding
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.google.firebase.Timestamp



/**
 * This class converts a to do item data class to a recyclerview of view holder items.


 * it has a view holder inner lass that inherits form the view holder constructor
 *  * @param dataSet Array of to-do items to be displayed.
 *  * @param onItemClicked Function to be called when a to-do item is clicked.
 */
class ToDoAdapter(var dataSet: List<ToDoItem>, private val onItemClicked: (ToDoItem) -> Unit) :
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>()
{

    class ViewHolder(val binding: ToDoRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder
    {

        val binding = ToDoRowBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    /**
     * Binds data from the dataSet to the views in the ViewHolder.
     * Sets up the to-do item's title, completion status, and due date.
     * Adds click listeners and updates the UI based on item state.
     *
     *
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var item = dataSet[position];
        viewHolder.itemView.setOnClickListener {
            onItemClicked(item);
        };
        viewHolder.binding.todoText.text = item.title ?: "No Title" // Default title if null
        val isDone = item.isDone ?: false // Default to false if null
        viewHolder.binding.todoSwitch.isChecked = isDone
        applyStrikeThrough(viewHolder.binding.todoText, isDone)

        // then listen for changes
        viewHolder.binding.todoSwitch.setOnCheckedChangeListener { _, isChecked ->
            item.isDone = isChecked;

            applyStrikeThrough(viewHolder.binding.todoText, isChecked);

            // Update in Firestore
            item.id?.let { toDoId ->
                val firestore = FireStoreDataManager()
                firestore.updateToDoIsDone(toDoId, isChecked) { success ->
                    if (!success) {
                        Toast.makeText(viewHolder.itemView.context, "Opps something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        };


        item.dueDate?.let { timestamp ->
            // Convert Firebase Timestamp to java.util.Date, then to java.time.Instant
            val instant = timestamp.toDate().toInstant()
            val dueDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
            viewHolder.binding.todoDueDate.text = dueDate.format(DateTimeFormatter.ofPattern("E MMM dd"))
            val daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), dueDate)
            viewHolder.binding.todoDueDate.setTextColor(
                when {
                    daysUntilDue < 0 -> Color.parseColor("#7f0205") // Overdue
                    daysUntilDue <= 7 -> Color.parseColor("#FF5722") // Due within a week
                    else -> Color.parseColor("#689F38") // More than a week away
                }
            )
        } ?: run {
            viewHolder.binding.todoDueDate.visibility = View.GONE
        }

    }

    /**Applies or removes a strike-through effect on the to-do item's title based on its completion status.
     *
     *  @param textView The TextView displaying the to-do item's title.
     *  @param isDone Whether the to-do item is marked as completed.
     * src:https://stackoverflow.com/questions/9786544/creating-a-strikethrough-text
     */
    private fun applyStrikeThrough(textView: TextView, isDone: Boolean) {
        if (isDone) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG;
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv();
        }
    }

    fun updateDataSet(newDataSet: List<ToDoItem>) {
        this.dataSet = newDataSet
        notifyDataSetChanged() // Notify any registered observers that the data set has changed.
    }
    fun getItemById(position: Int): ToDoItem? {
        return if (position >= 0 && position < dataSet.size) dataSet[position] else null
    }



    override fun getItemCount() = dataSet.size
}