package com.example.booksar.models

data class FireBaseBook(
    var id: String = "",
    var coverUrl: String = "",
    var x: Float = 0f,
    var y: Float = 0f,
    val rotation: QuaternionCustom = QuaternionCustom(0.0f, 0.0f, 0.0f, 0f)
)
