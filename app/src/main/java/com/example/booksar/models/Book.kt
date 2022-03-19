package com.example.booksar.models

import android.graphics.Color
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3

data class Book (
    val pages: Int?=100,
    val cover: BookCover,
    val author: String = "Amelia Forgacs",
    val title: String = "The best AR Book",
    val size: Vector3,
    val position: Vector3,
    val rotation: Quaternion,
    var coverWidth: Int = 1,
    var coverHeight: Int = 1,
    var coverColor: Int = Color.WHITE
    )