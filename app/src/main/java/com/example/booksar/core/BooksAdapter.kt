package com.example.booksar.core

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.booksar.R
import com.example.booksar.models.Book
import com.squareup.picasso.Picasso


class BooksAdapter(private val booksList: List<Book>) :
    RecyclerView.Adapter<BooksAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookModel = booksList[position]

        Picasso.get()
            .load(bookModel.cover.url)
            .into(holder.imageView)
        // sets the text to the textview from our itemHolder class
        holder.titleView.text = bookModel.title
        holder.authorView.text = bookModel.author
    }

    override fun getItemCount(): Int {
        return booksList.count()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.cover)
        val titleView: TextView = itemView.findViewById(R.id.title)
        val authorView: TextView = itemView.findViewById(R.id.author)
    }
}