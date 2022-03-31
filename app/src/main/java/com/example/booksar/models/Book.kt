package com.example.booksar.models

import android.graphics.Color
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import kotlin.random.Random

data class Book (
    val pages: Int?=100,
    val cover: BookCover,
    val author: String = "Amelia Forgacs" + Random.nextInt(0, 100),
    val title: String = "The best AR Book" + Random.nextInt(0, 100),
    val size: Vector3,
    val position: Vector3,
    val rotation: Quaternion,
    var coverWidth: Int = 1,
    var coverHeight: Int = 1,
    var coverColor: Int = Color.WHITE
    )
{
    companion object{
        fun createBook(isTemplate: Boolean = true, bookUrl: String = "C:\\Users\\Amelia\\Desktop\\Main\\GIT-Repositories\\BooksAR\\app\\src\\main\\assets\\atomic_habits_cover.jpg"
                       , qx: Float = 0f, qy: Float = 0f, qz: Float = 0f): Book {
            return Book(
                500,
                size = Vector3(10.14903799f, 10.038000144f, 0.2450379f),
                position = Vector3(qx, qy, qz),
                rotation = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 0f),
                cover = BookCover().apply { NeedDefaultCover = isTemplate; url = bookUrl}
            )
        }
    }
}