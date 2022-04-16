package com.example.booksar.models

import android.graphics.Color
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.io.Serializable
import java.util.*
import kotlin.random.Random

data class Book(
    var id: String = "",
    val pages: Int? = 100,
    val cover: BookCover = BookCover(),
    val author: String = "Amelia Forgacs" + Random.nextInt(0, 100),
    val title: String = "The best AR Book" + Random.nextInt(0, 100),
    val size: Vector3Custom = Vector3Custom(10f,10f,10f),
    val position: Vector3Custom = Vector3Custom(10f,10f,10f),
    var rotation: QuaternionCustom = QuaternionCustom(0.0f, 0.0f, 0.0f, 0f),
    var x: Float = 0f,
    var y: Float = 0f,
    var coverWidth: Int = 1,
    var coverHeight: Int = 1,
    var coverColor: Int = Color.WHITE
) : Serializable {

    companion object {
        fun createBook(
            isTemplate: Boolean = false,
            bookUrl: String = "https://mcdn.elefant.ro/mnresize/1500/1500/is/product-images/carte-straina/gardners/20200512/366d8ad1/e164/4e11/80a9/1f7279049d34/366d8ad1-e164-4e11-80a9-1f7279049d34_1.jpg",
            author: String = "",
            title: String = "",
            qx: Float = 0f,
            qy: Float = 0f,
            qz: Float = 0f
        ): Book {
            return Book(
                UUID.randomUUID().toString(),
                500,
                title = title,
                author = author,
                size = Vector3Custom(10.14903799f, 10.038000144f, 0.2450379f),
                position = Vector3Custom(qx, qy, qz),
                cover = BookCover().apply { NeedDefaultCover = isTemplate; url = bookUrl }
            )
        }


        fun createBookFromDocument(document: QueryDocumentSnapshot): Book {
            val rotationMap = document.data["rotation"] as HashMap<*, *>

            val book = Book().apply {
                id = document.data["id"].toString()
                x = (document.data["x"] as Double).toFloat()
                y = (document.data["y"] as Double).toFloat()
                rotation = QuaternionCustom(
                    (rotationMap["x"] as Double).toFloat(),
                    (rotationMap["y"] as Double).toFloat(),
                    (rotationMap["z"] as Double).toFloat(),
                    (rotationMap["w"] as Double).toFloat()
                )
                cover.url = document.data["coverUrl"].toString()
                cover.NeedDefaultCover = false
            }
            return book
        }
    }
}