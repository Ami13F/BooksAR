package com.example.booksar.models

import android.graphics.Color
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3

data class Book (
    val pages: Int?=100,
    val cover: String = "",
    val size: Vector3,
    val position: Vector3,
    val rotation: Quaternion,
    val coverUrl: String? = "",
    var coverWidth: Int = 1,
    var coverHeight: Int = 1,
    var coverColor: Int = Color.WHITE
    )