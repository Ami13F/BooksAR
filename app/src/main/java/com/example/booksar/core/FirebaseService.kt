package com.example.booksar.core

import android.util.Log
import android.view.MotionEvent
import com.example.booksar.models.Book
import com.example.booksar.models.Book.Companion.createBookFromDocument
import com.example.booksar.models.FireBaseBook
import com.example.booksar.models.QuaternionCustom
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseService {
    private val database = FirebaseFirestore.getInstance()
    private val collectionPath = "models"

    fun saveBook(book: Book) {
        val ref = database.collection(collectionPath).document()
        val id: String = ref.id
        book.id = id
        ref.set(
            FireBaseBook(
                id,
                book.cover.url
            )
        )
    }

    fun fetchBooks(bookArService: BookArService) {
        database.collection(collectionPath)
            .get()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("[ami]", "Error getting books.", task.exception)
                    return@addOnCompleteListener
                }

                for (document in task.result) {
                    val book = createBookFromDocument(document)
                    bookArService.createObject(book, false)
                }
            }
    }

    fun updateBook(motionEvent: MotionEvent, bookNode: TransformableNode, bookId: String) {
        database.collection("models")
            .document(bookId)
            .update(
                mapOf(
                    "x" to motionEvent.x,
                    "y" to motionEvent.y,
                    "rotation" to QuaternionCustom(
                        bookNode.localRotation.x,
                        bookNode.localRotation.y,
                        bookNode.localRotation.z,
                        bookNode.localRotation.w
                    )
                )
            )
    }

    fun deleteAll() {
        val batch = database.batch()
        database.collection(collectionPath).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.forEach {
                        batch.delete(it.reference)
                    }
                }
                batch.commit()
            }
    }
}