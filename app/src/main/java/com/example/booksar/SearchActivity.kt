package com.example.booksar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booksar.core.BooksAdapter
import com.example.booksar.models.Book
import com.example.booksar.web.HtmlExtractorService
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class SearchActivity : AppCompatActivity(), MaterialSearchBar.OnSearchActionListener {
    lateinit var searchBar: MaterialSearchBar
    lateinit var recycleView: RecyclerView
    private var htmlExtractor = HtmlExtractorService()
    private var data = mutableListOf<Book>()

    private var adapter = BooksAdapter(data)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar!!.hide()

        recycleView = findViewById(R.id.searchRecyclerView)
        recycleView.layoutManager = LinearLayoutManager(this)

        searchBar = findViewById(R.id.searchBar)
        searchBar.openSearch()

        val liveData = MutableLiveData<MutableList<Book>>()

        recycleView.adapter = adapter

        liveData.observe(this) { mutableBooks ->
            adapter.updateBooks(mutableBooks)
        }

        GlobalScope.launch {
            htmlExtractor.extract().forEach { goodReadsBook ->
                data.add(
                    Book.createBook(
                        bookUrl = goodReadsBook.cover,
                        author = goodReadsBook.authors,
                        title = goodReadsBook.title
                    )
                )
            }
            liveData.postValue(data)
        }

        searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Log.d(
                    SearchActivity::class.java.name,
                    javaClass.simpleName + " text changed " + searchBar.text
                )

                liveData.postValue(data
                    .filter {
                        filterBook(it)
                    }.toMutableList()
                )
            }

            private fun filterBook(it: Book) =
                (it.author.contains(searchBar.text, ignoreCase = true)
                        || it.title.contains(searchBar.text, ignoreCase = true))

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    override fun onSearchStateChanged(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onSearchConfirmed(text: CharSequence?) {
        TODO("Not yet implemented")
    }

    override fun onButtonClicked(buttonCode: Int) {
        TODO("Not yet implemented")
    }
}