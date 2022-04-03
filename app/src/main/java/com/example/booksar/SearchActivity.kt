package com.example.booksar

import android.content.Context
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class SearchActivity : AppCompatActivity(), MaterialSearchBar.OnSearchActionListener {
    val coverUri = listOf("https://mcdn.elefant.ro/mnresize/1500/1500/is/product-images/carte-ro/53b53f96/41cc/4a6c/b33c/6430f5e82efe/53b53f96-41cc-4a6c-b33c-6430f5e82efe_1.jpg",
    "https://mcdn.elefant.ro/mnresize/1500/1500/images/15/1280115/visul-numarul-9_1_fullsize.jpg",
        "https://www.libris.ro/img/pozeprod/59/1002/39/10893783.jpg",
    "https://cdn.dc5.ro/img-prod/1977820-0.jpeg",
        "https://cdn.dc5.ro/img-prod/5193-1.jpeg")

    lateinit var searchBar: MaterialSearchBar
    lateinit var recycleView: RecyclerView
    private var htmlExtractor = HtmlExtractorService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar!!.hide()

        val activity = this

        recycleView = findViewById(R.id.searchRecyclerView)
        recycleView.layoutManager = LinearLayoutManager(this)

        searchBar = findViewById(R.id.searchBar)
        searchBar.openSearch()
        // ArrayList of class ItemsViewModel
        val data = ArrayList<Book>()

        // This loop will create 20 Views containing
        // the image with the count of view
//        for (i in 1..20) {
//            data.add(Book.createBook(i % 2 == 0, coverUri[Random.nextInt(0, 4)]))
//        }
        val adapter = BooksAdapter(data)

        GlobalScope.launch {
            htmlExtractor.extract().forEach { goodReadsBook ->
                data.add(Book.createBook(
                    bookUrl= goodReadsBook.cover,
                    author = goodReadsBook.authors,
                    title = goodReadsBook.title
                ))
                adapter.filterList(data)
            }
        }

        recycleView.adapter = adapter
//        adapter.getBookMutableLiveData().observe(activity.applicationContext, booksListUpdateObserver )
        searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Log.d(
                    SearchActivity::class.java.name,
                    javaClass.simpleName + " text changed " + searchBar.text
                )

                adapter.filterList(data
                    .filter { it.author.contains(searchBar.text)
                            || it.title.contains(searchBar.text)}
                    .toList())
            }

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