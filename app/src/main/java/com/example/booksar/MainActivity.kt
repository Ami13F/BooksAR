package com.example.booksar

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booksar.core.BookArService
import com.google.ar.sceneform.ux.ArFragment

class MainActivity : AppCompatActivity() {

    lateinit var mainFragment: ArFragment
    private lateinit var bookArService : BookArService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment
        bookArService = BookArService(this, mainFragment)

        mainFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            Toast.makeText(this.baseContext, "pressed", Toast.LENGTH_SHORT).show()
            bookArService.createObject(Uri.parse("models/book_small.glb"))
        }

    }
}