package ca.lakeheadu.mirantodoapp

import com.google.firebase.Timestamp

/**
 * This data class defines a to do item, it has 4 properties and stores date ad a LocaleDate
 */
import com.google.firebase.firestore.PropertyName

data class ToDoItem(
    val id: String? = null,
    val title: String? = null,
    @get:PropertyName("isDone") @set:PropertyName("isDone") var isDone: Boolean? = null,
    val dueDate: Timestamp? = null,
    val notes: String? = null
)
