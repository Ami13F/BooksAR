package com.example.booksar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booksar.core.BookArFragment
import com.example.booksar.core.BookArService
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var mainFragment: BookArFragment
    private lateinit var bookArService: BookArService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as BookArFragment

        bookArService = BookArService(this, mainFragment)
        mainFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            Toast.makeText(this.baseContext, "pressed", Toast.LENGTH_SHORT).show()
            bookArService.createObject(BOOK_URI)
        }

        val searchBtn = findViewById<FloatingActionButton>(R.id.searchBookBtn)
        searchBtn.setOnClickListener{
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    companion object {
        val BOOK_URI: Uri = Uri.parse("models/book_small.glb")
    }
}