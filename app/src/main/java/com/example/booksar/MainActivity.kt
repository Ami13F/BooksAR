package com.example.booksar

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.PixelCopy
import androidx.appcompat.app.AppCompatActivity
import com.example.booksar.core.BookArService
import com.example.booksar.core.BooksArFragment
import com.example.booksar.core.FirebaseService
import com.example.booksar.models.Book
import com.github.clans.fab.FloatingActionButton
import com.google.ar.sceneform.ArSceneView
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

        val shareButton = findViewById<FloatingActionButton>(R.id.shareBtn)
        shareButton.setOnClickListener {
            takeScreenShot(mainFragment.arSceneView)
        }
    }

    private fun takeScreenShot(view: ArSceneView): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        PixelCopy.request(
            view,
            bitmap, { copyResult: Int ->
                if (copyResult == PixelCopy.SUCCESS) {
                    shareContent(bitmap)
                }
            }, Handler()
        )
        return bitmap
    }

    private fun shareContent(bitmap: Bitmap) {
        val bitmapPath: String = MediaStore.Images.Media.insertImage(
            contentResolver, bitmap, "Share", "Share this with: "
        )

        val uri = Uri.parse(bitmapPath)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "App")
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Look at my virtual library.")
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, "Share"))
    }
}