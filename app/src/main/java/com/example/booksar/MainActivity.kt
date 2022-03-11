package com.example.booksar

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booksar.core.BookArService
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class MainActivity : AppCompatActivity() {

    private lateinit var mainFragment: ArFragment
    private lateinit var bookArService: BookArService
//    private lateinit var bookObject: ModelRenderable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment
        bookArService = BookArService(this, mainFragment)
//        GlobalScope.async {
//            bookObject = bookArService.createBookObjectAwait(BOOK_URI)
//        }
        mainFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            Toast.makeText(this.baseContext, "pressed", Toast.LENGTH_SHORT).show()
            bookArService.createObject(BOOK_URI)
        }

    }

    companion object {
        val BOOK_URI: Uri = Uri.parse("models/book_small.glb")
    }
}