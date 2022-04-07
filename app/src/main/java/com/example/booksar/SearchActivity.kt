package com.example.booksar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booksar.core.BooksAdapter
import com.example.booksar.models.Book
import com.example.booksar.web.HtmlExtractorService
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class SearchActivity : AppCompatActivity(), MaterialSearchBar.OnSearchActionListener {
    lateinit var searchBar: MaterialSearchBar
    lateinit var recycleView: RecyclerView
    private var htmlExtractor = HtmlExtractorService()
    private var data = mutableListOf<Book>()
    private val liveData = MutableLiveData<MutableList<Book>>()

    private var adapter = BooksAdapter(this, data)

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar!!.hide()

        recycleView = findViewById(R.id.searchRecyclerView)
        recycleView.layoutManager = LinearLayoutManager(this)

        searchBar = findViewById(R.id.searchBar)
        searchBar.openSearch()

        recycleView.adapter = adapter

        liveData.observe(this) { mutableBooks ->
            adapter.updateBooks(mutableBooks)
        }

        searchBar.addTextChangeListener(object : TextWatcher {
            var timer = Timer()
            val DELAY: Long = 1000 // Milliseconds

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Log.d(
                    SearchActivity::class.java.name,
                    javaClass.simpleName + " text changed " + searchBar.text
                )
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            data.clear()
                            GlobalScope.launch {
                                htmlExtractor.extract(searchBar.text)
                                    .forEach { goodReadsBook ->
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
                        }
                    },
                    DELAY
                )

            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    override fun onSearchStateChanged(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onSearchConfirmed(text: CharSequence?) {

    }

    override fun onButtonClicked(buttonCode: Int) {
        TODO("Not yet implemented")
    }
}