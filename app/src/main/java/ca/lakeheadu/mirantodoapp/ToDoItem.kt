package ca.lakeheadu.mirantodoapp

import java.time.LocalDate

data class ToDoItem(
    val title:String,
    val dueDate:LocalDate,
    val isDone: Boolean
)
