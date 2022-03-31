package com.example.booksar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booksar.core.BooksAdapter
import com.example.booksar.models.Book
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlin.random.Random


class SearchActivity : AppCompatActivity(), MaterialSearchBar.OnSearchActionListener {
    val coverUri = listOf("https://mcdn.elefant.ro/mnresize/1500/1500/is/product-images/carte-ro/53b53f96/41cc/4a6c/b33c/6430f5e82efe/53b53f96-41cc-4a6c-b33c-6430f5e82efe_1.jpg",
    "https://mcdn.elefant.ro/mnresize/1500/1500/images/15/1280115/visul-numarul-9_1_fullsize.jpg",
        "https://www.libris.ro/img/pozeprod/59/1002/39/10893783.jpg",
    "https://cdn.dc5.ro/img-prod/1977820-0.jpeg",
        "https://cdn.dc5.ro/img-prod/5193-1.jpeg")

    lateinit var searchBar: MaterialSearchBar
    lateinit var recycleView: RecyclerView

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
        for (i in 1..20) {
            data.add(Book.createBook(i % 2 == 0, coverUri[Random.nextInt(0, 4)]))
        }
        val adapter = BooksAdapter(data)
        recycleView.adapter = adapter

        searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Log.d(
                    SearchActivity::class.java.name,
                    javaClass.simpleName + " text changed " + searchBar.text
                )
                Toast.makeText(activity, "Search: ${searchBar.text}", Toast.LENGTH_SHORT).show()
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