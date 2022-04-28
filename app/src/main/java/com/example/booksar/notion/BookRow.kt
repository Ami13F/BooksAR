package com.example.booksar.notion

import com.google.firebase.firestore.QueryDocumentSnapshot
import java.io.Serializable

data class BookRow(
    var title: String,
    var author: String,
    var summary: String,
    var reviewStartNumbers: Int,
    var genres: List<String?>
) : Serializable {
    companion object {
        fun createNotionBookRowFromDocument(document: QueryDocumentSnapshot): BookRow {
            return BookRow(
                title = document.data["title"].toString(),
                author = document.data["author"].toString(),
                summary = document.data["summary"].toString(),
                reviewStartNumbers = (document.data["reviewStartNumbers"] as Long?)?.toInt() ?: 0,
                genres = document.data["genres"] as List<String>? ?: emptyList()
            )
        }
    }
}