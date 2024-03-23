package ca.lakeheadu.mirantodoapp

import java.time.LocalDate

data class ToDoItem(
    val title:String,
    var isDone: Boolean,
    val dueDate:LocalDate?=null,
    val notes:String?=null

)
