package com.example.booksar.models

import android.graphics.Color
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import kotlin.random.Random
import java.io.Serializable

data class Book(
    val pages: Int? = 100,
    val cover: BookCover,
    val author: String = "Amelia Forgacs" + Random.nextInt(0, 100),
    val title: String = "The best AR Book" + Random.nextInt(0, 100),
    val size: Vector3Custom,
    val position: Vector3Custom,
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
                500,
                title = title,
                author = author,
                size = Vector3Custom(10.14903799f, 10.038000144f, 0.2450379f),
                position = Vector3Custom(qx, qy, qz),
                cover = BookCover().apply { NeedDefaultCover = isTemplate; url = bookUrl }
            )
        }
    }
}