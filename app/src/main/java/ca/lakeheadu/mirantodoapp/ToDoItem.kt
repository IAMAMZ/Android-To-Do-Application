package ca.lakeheadu.mirantodoapp

import com.google.firebase.Timestamp

/**
 * This data class defines a to do item, it has 4 properties and stores date ad a LocaleDate
 */
data class ToDoItem(
    val title:String?=null,
    var isDone: Boolean?=null,
    val dueDate:Timestamp?=null,
    val notes:String?=null

)
