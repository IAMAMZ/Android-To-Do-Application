package ca.lakeheadu.mirantodoapp

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class FireStoreDataManager {


    private val db  = FirebaseFirestore.getInstance();

    private val collectionRef = db.collection("toDos");

    fun getToDos(onComplete: (List<ToDoItem>) ->Unit){

        collectionRef.get()
            .addOnSuccessListener { result->
                val todos = result.mapNotNull { it.toObject<ToDoItem>() }
                onComplete(todos);
            }
            .addOnFailureListener{
                onComplete(emptyList())
            }

    }
}