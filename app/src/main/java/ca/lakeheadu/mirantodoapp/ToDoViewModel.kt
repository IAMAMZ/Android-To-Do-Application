package ca.lakeheadu.mirantodoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


/**
 * ViewModel for managing UI-related data in a lifecycle-conscious way.
 * It survives configuration changes like screen rotations, thus holding data that is relevant to the UI.
 *
 * @param application The application context, used for AndroidViewModel.
 */
class ToDoViewModel(application: Application) : AndroidViewModel(application) {

    // A private mutable LiveData that holds the details of the to-do item to navigate to.
    private val _navigateToDetails = MutableLiveData<ToDoItem?>()


    // Public LiveData that external classes can observe. It exposes _navigateToDetails as immutable LiveData.
    val navigateToDetails: LiveData<ToDoItem?>
        get() = _navigateToDetails

    /**
     * Called when a to-do item is clicked.
     * Updates _navigateToDetails to hold the clicked to-do item, triggering any observers, like UI components, to react.
     *
     * @param toDoItem The to-do item that was clicked.
     */
    fun onToDoClicked(toDoItem: ToDoItem) {
        _navigateToDetails.value = toDoItem
    }

    /**
     * Resets the navigation data to null after navigation is done.
     * This is to ensure we don't accidentally navigate twice if the user returns to the screen where the navigation was triggered.
     */
    fun onToDoDetailsNavigated() {
        _navigateToDetails.value = null
    }
}
