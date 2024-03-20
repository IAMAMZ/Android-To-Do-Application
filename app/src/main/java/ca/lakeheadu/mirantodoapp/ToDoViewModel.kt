package ca.lakeheadu.mirantodoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ToDoViewModel(application: Application) : AndroidViewModel(application) {
    private val _navigateToDetails = MutableLiveData<ToDoItem?>()
    val navigateToDetails: LiveData<ToDoItem?>
        get() = _navigateToDetails

    fun onToDoClicked(toDoItem: ToDoItem) {
        _navigateToDetails.value = toDoItem
    }

    fun onToDoDetailsNavigated() {
        _navigateToDetails.value = null
    }
}
