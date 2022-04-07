package com.example.booksar.web

import android.util.Log
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class HtmlExtractorService {
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun extract(queryBook : String): MutableList<GoodReadsBook>
    {
        val goodReadsBookArray = mutableListOf<GoodReadsBook>()

        withContext(Dispatchers.IO) {
            val doc = Jsoup.connect("https://www.goodreads.com/search?q=$queryBook").get()

            doc.select("tr")
                .forEach { e ->
                    val goodReadsBook = GoodReadsBook().apply {
                        cover = e.select("img.bookCover")
                            .mapNotNull { col -> col.attr("src") }[0]
                            .replace("SY75","SY200") //resize image

                        title = e.select("a.bookTitle span[itemprop='name']")[0].html()
                    }

                    val array : MutableList<String> = mutableListOf()
                    e.select("a.authorName span[itemprop='name']")
                        .forEach { author ->
                            array.add(author.html())
                        }

                    goodReadsBook.authors = array.joinToString()

                    Log.w("[ami]", goodReadsBook.toString())
                    goodReadsBookArray.add(goodReadsBook)
                }
        }
        return goodReadsBookArray
    }
}