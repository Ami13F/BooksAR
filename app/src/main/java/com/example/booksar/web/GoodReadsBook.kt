package com.example.booksar.web

class GoodReadsBook(var title: String="", var authors: String = "", var cover: String=""){

    override fun toString(): String {
        var authorsTemp = ""
//        authors.forEach { authorsTemp+= "$it, " }
        return "Title: $title \n Author: $authors \n Cover: $cover"
    }
}