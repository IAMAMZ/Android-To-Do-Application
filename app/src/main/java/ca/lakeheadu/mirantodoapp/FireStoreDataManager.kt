package ca.lakeheadu.mirantodoapp

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

/***
 * This class acts as a utility class to handle all firestore database operations
 */
class FireStoreDataManager {

    // get an instance of the database
    private val db = FirebaseFirestore.getInstance();

    // get the reference to the collection in the database (equivalent to table)
    private val collectionRef = db.collection("toDos");

    /**
     * This function returns all the do items inside the
     * collection and adds the List of to dos into the
     * argument callback or empty list if it fails
     */
    fun getToDos(onComplete: (List<ToDoItem>) -> Unit) {
        collectionRef.get()
            .addOnSuccessListener { result ->
                val todos = result.map { documentSnapshot ->
                    documentSnapshot.toObject<ToDoItem>().copy(id = documentSnapshot.id) // adds the document id to the to do
                }
                onComplete(todos)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }


    /****
     * Takes a to do item object and saves it to the database
     *
     * puts true in the callback argument  upon success otherwise false
     */

    fun saveToDo(toDoItem: ToDoItem, onComplete: (Boolean) -> Unit) {
        collectionRef.add(toDoItem)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    /**
     * Takes a to do item id and then delete that,
     * if successful it will run the callback supplying true as the argument
     * otherwise it will run callback supplying false
     */
    fun deleteToDo(toDoId: String, onComplete: (Boolean) -> Unit) {
        collectionRef.document(toDoId).delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Document with id $toDoId successfully deleted")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.d("Firestore", "Error deleting  $toDoId", e)
                onComplete(false)
            }
    }

    /**
     * This function is to update the to do item to mark it as done,
     * takes to do id and is done and based on the isDone second argument it will update
     * the idDone property of the given to do item with the supplied id
     */
    fun updateToDoIsDone(toDoId: String, isDone: Boolean, callback: (Boolean) -> Unit) {

        val documentRef = collectionRef.document(toDoId)

        Log.d("FireStoreDataManager", "Updating isDone for document: $toDoId")

        documentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                Log.d("FireStoreDataManager", "Document exists, updating isDone...")

                documentRef.update("isDone", isDone)
                    .addOnSuccessListener {
                        Log.d("FireStoreDataManager", "Document updated successfully")
                        callback(true)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("FireStoreDataManager", "Error updating document", exception)
                        callback(false)
                    }
            } else {
                Log.w("FireStoreDataManager", "Document does not exist: $toDoId")
                callback(false)
            }
        }.addOnFailureListener { exception ->
            Log.e("FireStoreDataManager", "Error getting document", exception)
            callback(false)
        }
    }

    /**
     * Takes 5 parameters that defines a to do item and updates the to do item with
     * the supplied parameters
     */
    fun updateToDoItem(
        toDoId: String,
        title: String,
        notes: String,
        dueDate: com.google.firebase.Timestamp?,
        callback: (Boolean) -> Unit
    ) {
        val documentRef = collectionRef.document(toDoId)
        val updatedData = hashMapOf<String, Any>(
            "title" to title,
            "notes" to notes
        )

        // Add dueDate to the map only if it's not null
        dueDate?.let { updatedData["dueDate"] = it }

        documentRef.update(updatedData)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }


}

