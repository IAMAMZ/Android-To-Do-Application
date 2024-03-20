package ca.lakeheadu.mirantodoapp
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.lakeheadu.mirantodoapp.databinding.ToDoRowBinding

class ToDoAdapter(private val dataSet: Array<ToDoItem>, private val onItemClicked: (ToDoItem) -> Unit) :
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>()
{

    class ViewHolder(val binding: ToDoRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder
    {

        val binding = ToDoRowBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.setOnClickListener {
            onItemClicked(dataSet[position])
        }
        viewHolder.binding.todoText.text = dataSet[position].title
        val dueDate = dataSet[position].dueDate
        viewHolder.binding.todoSwitch.isChecked = dataSet[position].isDone;
        if (dueDate != null) {
            viewHolder.binding.todoDueDate.text = dueDate.toString()
        } else {
             viewHolder.binding.todoDueDate.visibility = View.GONE
        }
    }

    override fun getItemCount() = dataSet.size
}