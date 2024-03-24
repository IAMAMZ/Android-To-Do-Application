package ca.lakeheadu.mirantodoapp

import java.time.LocalDate

/**
 * This data class defines a to do item, it has 4 properties and stores date ad a LocaleDate
 */
data class ToDoItem(
    val title:String,
    var isDone: Boolean,
    val dueDate:LocalDate?=null,
    val notes:String?=null

)
