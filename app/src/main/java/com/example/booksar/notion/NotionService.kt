package com.example.booksar.notion

import com.example.booksar.BuildConfig
import notion.api.v1.NotionClient

class NotionService {
    fun main() {
        val client = NotionClient(token = BuildConfig.TOKEN)
        client.use {
            val db = client.queryDatabase(BuildConfig.DATABASE_ID)
            val title = "Manon"

            val bookByTitle = db.results.asSequence().map { it.properties }
                .filter { it["Name"]?.title?.firstOrNull()?.plainText?.contains(title) ?: false }

            val summary = bookByTitle
                .map { it["Comments"] }.mapNotNull { it?.richText }.filter { it.isNotEmpty() }
                .mapNotNull { it.first() }.map { it.plainText }
                .toList()

            val review = bookByTitle.map { it["Score"] }.mapNotNull { it?.select }
                .map { it.name?.length ?: 0 }
                .toList()

            val genres = bookByTitle.map { it["Genre"] }.map { it?.multiSelect }
                .filter { it?.isNotEmpty() ?: false }.mapNotNull { el -> el?.map { it.name } }
        }
    }
}

fun main() = NotionService().main()
