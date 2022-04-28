package com.example.booksar.database

import android.util.Log
import android.view.MotionEvent
import com.example.booksar.core.BookArService
import com.example.booksar.models.Book
import com.example.booksar.models.Book.Companion.createBookFromDocument
import com.example.booksar.models.BookCover
import com.example.booksar.models.FireBaseBook
import com.example.booksar.models.QuaternionCustom
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class FirebaseService {
    private val database = FirebaseFirestore.getInstance()
    private val collectionPath = "books"

    fun saveBook(book: Book) {
        val ref = database.collection(collectionPath).document()
        val id: String = ref.id
        book.id = id
        ref.set(
            book
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
        database.collection(collectionPath)
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

    fun getBook(bookId: String, bookNode: TransformableNode, bookArService: BookArService) {

        database.collection(collectionPath)
            .whereEqualTo("id", bookId)
            .get()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("[ami]", "Error getting books.", task.exception)
                    return@addOnCompleteListener
                }

                for (document in task.result) {
                    val book = createBookFromDocument(document)
                    bookArService.createBookSummary(bookNode, book)
                }
            }
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