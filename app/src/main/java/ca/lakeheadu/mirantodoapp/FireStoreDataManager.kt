package ca.lakeheadu.mirantodoapp

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class FireStoreDataManager {


    private val db  = FirebaseFirestore.getInstance();

    private val collectionRef = db.collection("toDos");

    fun getToDos(onComplete: (List<ToDoItem>) ->Unit){
        collectionRef.get()
            .addOnSuccessListener { result->
                val todos = result.map { documentSnapshot ->
                    documentSnapshot.toObject<ToDoItem>().copy(id = documentSnapshot.id)
                }
                onComplete(todos)
            }
            .addOnFailureListener{
                onComplete(emptyList())
            }
    }

    fun saveToDo(toDoItem: ToDoItem, onComplete: (Boolean) -> Unit) {
        collectionRef.add(toDoItem)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
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



}