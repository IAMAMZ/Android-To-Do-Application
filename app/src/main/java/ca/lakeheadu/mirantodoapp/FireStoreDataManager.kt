package ca.lakeheadu.mirantodoapp

import android.util.Log
import com.google.firebase.Timestamp
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

    fun updateToDoItem(toDoId: String, title: String, notes: String, dueDate: Timestamp, callback: (Boolean) -> Unit) {
        val documentRef = collectionRef.document(toDoId)
        documentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val updatedData = hashMapOf(
                    "title" to title,
                    "notes" to notes,
                    "dueDate" to dueDate
                )
                documentRef.update(updatedData as Map<String, Any>)
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            } else {
                callback(false)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

    }

