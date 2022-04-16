package com.example.booksar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.booksar.core.BookArService
import com.example.booksar.core.BooksArFragment
import com.example.booksar.core.FirebaseService
import com.example.booksar.models.Book
import com.github.clans.fab.FloatingActionButton
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {

    private lateinit var mainFragment: BooksArFragment
    private lateinit var bookArService: BookArService
    private var firebaseService = FirebaseService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        mainFragment =
            supportFragmentManager.findFragmentById(R.id.arFragment) as BooksArFragment

        bookArService = BookArService(this, mainFragment)
        mainFragment.bookArService = bookArService

        mainFragment.setOnTapArPlaneListener { _, _, _ ->
            val book = intent.getSerializableExtra("book") as Book?
            if (book != null)
                bookArService.createObject(book)
        }


        val searchBtn = findViewById<FloatingActionButton>(R.id.searchBookBtn)
        searchBtn.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        val resolveBtn = findViewById<FloatingActionButton>(R.id.resolveBtn)
        resolveBtn.setOnClickListener {
            firebaseService.fetchBooks(bookArService)
        }

        val clearButton = findViewById<FloatingActionButton>(R.id.clearBtn)
        clearButton.setOnClickListener {
            firebaseService.deleteAll()
        }
    }
}