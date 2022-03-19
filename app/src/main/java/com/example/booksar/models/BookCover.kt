package com.example.booksar.models

data class BookCover(
    val width: Int = 100,
    val height: Int = 100,
    val textColor: Int = -1, //white default
    val url: String =
    //"https://mcdn.elefant.ro/mnresize/1500/1500/images/15/1280115/visul-numarul-9_1_fullsize.jpg",
        "https://www.libris.ro/img/pozeprod/59/1002/39/10893783.jpg",
    //"https://cdn.dc5.ro/img-prod/1977820-0.jpeg",
//        "https://cdn.dc5.ro/img-prod/5193-1.jpeg",
    //"https://mcdn.elefant.ro/mnresize/1500/1500/is/product-images/carte-straina/gardners/20200512/366d8ad1/e164/4e11/80a9/1f7279049d34/366d8ad1-e164-4e11-80a9-1f7279049d34_1.jpg",
    /*
        true if the book from api has no cover
     */
    var NeedDefaultCover: Boolean = false

)
