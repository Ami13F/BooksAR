package com.example.booksar.database

import android.util.Log
import com.example.booksar.core.BookArService
import com.example.booksar.models.Book
import com.example.booksar.notion.BookRow
import com.google.firebase.firestore.FirebaseFirestore

class NotionFirebase {
    private val database = FirebaseFirestore.getInstance()
    private val collectionPath = "notionRow"

    private var books = mutableListOf<BookRow>()

    fun saveNotionRow(notionRow: BookRow) {
        val ref = database.collection(collectionPath).document()
        ref.set(
            notionRow
        )
    }

    fun fetchNotionRow() {
        database.collection(collectionPath)
            .get()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("[ami]", "Error getting books.", task.exception)
                    return@addOnCompleteListener
                }
                val bookList = mutableListOf<BookRow>()
                for (document in task.result) {
                    val bookRow = BookRow.createNotionBookRowFromDocument(document)
                    bookList.add(bookRow)
                }
                populateRows(bookList)
            }
    }

    private fun populateRows(bookList: MutableList<BookRow>) {
        books = bookList
    }

    fun getBookRows(): MutableList<BookRow> {
        return books
    }

    fun removeAllBooks() {
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