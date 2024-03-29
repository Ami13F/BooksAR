package com.example.booksar.notion

import android.util.Log
import com.example.booksar.BuildConfig
import com.example.booksar.database.NotionFirebase
import notion.api.v1.NotionClient

class NotionService() {

    private var bookRows = mutableListOf<BookRow>()

    fun initializeNotionBooks() {
        val startTime = System.currentTimeMillis()
        NotionFirebase().removeAllBooks()

        val client = NotionClient(token = BuildConfig.TOKEN)
        client.use {
            val db = client.queryDatabase(BuildConfig.DATABASE_ID)

            db.results.asSequence().map { it.properties }
                .mapNotNull { it["Name"]?.title?.firstOrNull()?.plainText }
                .forEach { bookTitle ->
                    val bookByTitle = db.results.asSequence().map { it.properties }
                        .filter {
                            it["Name"]?.title?.firstOrNull()?.plainText?.contains(bookTitle)
                                ?: false
                        }

                    val author = db.results.asSequence().map { it.properties }
                        .mapNotNull { it["Author"]?.title?.firstOrNull()?.plainText }
                        .toList().firstOrNull() ?: ""

                    val summary = bookByTitle
                        .map { it["Comments"] }.mapNotNull { it?.richText }
                        .filter { it.isNotEmpty() }
                        .mapNotNull { it.first() }.map { it.plainText }
                        .toList().firstOrNull() ?: ""

                    val review = bookByTitle.map { it["Score"] }.mapNotNull { it?.select }
                        .map { it.name?.length ?: 0 }
                        .toList().firstOrNull() ?: 0

                    val genres = bookByTitle
                            .map { it["Genre"] }.map { it?.multiSelect }
                        .filter { it?.isNotEmpty() ?: false }
                        .mapNotNull { el -> el?.map { it.name } }
                        .toList().firstOrNull() ?: listOf()

//                    bookRows.add(
//                        BookRow(
//                            bookTitle,
//                            author = author,
//                            summary = summary,
//                            reviewStartNumbers = review,
//                            genres = genres
//                        )
//                    )
                    NotionFirebase().saveNotionRow(
                        BookRow(
                            bookTitle,
                            author = author,
                            summary = summary,
                            reviewStartNumbers = review,
                            genres = genres
                        )
                    )
                }
            Log.w(
                "[ami]",
                "Initialize notion data time: ${System.currentTimeMillis() - startTime} milliseconds"
            )
        }
        client.close()
    }

    fun getBookRows(): MutableList<BookRow> {
        return bookRows
    }
}