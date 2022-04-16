package com.example.booksar.models

import java.io.Serializable

data class BookCover(
    val width: Int = 100,
    val height: Int = 100,
    val textColor: Int = -1, //white default
    var url: String = "https://mcdn.elefant.ro/mnresize/1500/1500/is/product-images/carte-straina/gardners/20200512/366d8ad1/e164/4e11/80a9/1f7279049d34/366d8ad1-e164-4e11-80a9-1f7279049d34_1.jpg",
    /*
        true if the book from api has no cover
     */
    var NeedDefaultCover: Boolean = true

) : Serializable
