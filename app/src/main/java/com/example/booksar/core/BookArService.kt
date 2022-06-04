package com.example.booksar.core

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent.ACTION_UP
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.example.booksar.R
import com.example.booksar.database.FirebaseService
import com.example.booksar.database.NotionFirebase
import com.example.booksar.helpers.CoverHelper.Companion.createCoverFromImage
import com.example.booksar.helpers.CoverHelper.Companion.createTemplateViewCover
import com.example.booksar.helpers.ExceptionHelper.Companion.onException
import com.example.booksar.models.Book
import com.example.booksar.notion.BookRow
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.text.Normalizer
import java.util.concurrent.CompletableFuture


class BookArService(
    private val activity: Activity,
    private var fragment: ArFragment,
    private val notionService: NotionFirebase
) {

    companion object {
        val BOOK_URI: Uri = Uri.parse("models/book_small.glb")
    }

    private val firebaseService = FirebaseService()

    fun createObject(book: Book, isFirstTimeCreated: Boolean = true) {
        val frame = fragment.arSceneView.arFrame ?: return

        var x = book.x
        var y = book.y
        if (isFirstTimeCreated) {
            firebaseService.saveBook(book)
            val point = ArScreen.getScreenCenter(activity)
            x = point.x.toFloat()
            y = point.y.toFloat()
        }

        val hits = frame.hitTest(x, y)
        for (hit in hits) {
            val trackable = hit.trackable
            if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                displayObject(hit.createAnchor(), book)
            }
        }
    }

    private fun createBookObject(bookUrl: Uri): CompletableFuture<ModelRenderable> {
        return ModelRenderable.builder()
            .setSource(fragment.context, bookUrl)
            .setIsFilamentGltf(true)
            .build()
    }

    private fun displayObject(createAnchor: Anchor, book: Book) {
        createBookObject(BOOK_URI)
            .thenAccept {
                addObjectToScene(createAnchor, book, it)
            }
            .exceptionally {
                return@exceptionally onException(activity, it)
            }
    }

    private fun addObjectToScene(
        anchor: Anchor,
        book: Book,
        model: ModelRenderable
    ) {
        val anchorNode = AnchorNode(anchor)
        val bookNode = TransformableNode(fragment.transformationSystem)

        bookNode.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == ACTION_UP) {
                val id = bookNode.name ?: ""
                firebaseService.updateBook(motionEvent, bookNode, id) //book.id
                firebaseService.getBook(id, bookNode, this) //book.id
            }
            return@setOnTouchListener true
        }
        bookNode.localRotation =
            Quaternion(book.rotation.x, book.rotation.y, book.rotation.z, book.rotation.w)
        bookNode.name = book.id
        bookNode.localPosition = Vector3(0.0f, 0f, 0.0f)
        bookNode.localScale = Vector3(2f, 2f, 2f)
        bookNode.parent = anchorNode
        anchorNode.parent = fragment.arSceneView.scene

        val childNode = Node()
        childNode.parent = bookNode
        childNode.renderable = model
        childNode.localScale = Vector3(2f, 2f, 2f)

        loadViewForCover(book, childNode)

        bookNode.select()
    }

    fun createBookSummary(bookNode: TransformableNode, book: Book) {
        ViewRenderable.builder().setView(activity, R.layout.book_info)
            .build()
            .thenAccept { viewRenderable ->
                Log.w("[ami]", "bookInfo: ${book.title}")
                val bookRowNotion = notionService.getBookRows()
                    .firstOrNull {
                        b(it, book)
                    }

                val summary = viewRenderable.view.findViewById<TextView>(R.id.summaryText)
                val stars = viewRenderable.view.findViewById<RatingBar>(R.id.reviewStars)
                val infoView = viewRenderable.view.findViewById<LinearLayout>(R.id.infoView)
                val chipGroup = viewRenderable.view.findViewById<ChipGroup>(R.id.chipGroup)

                if (bookRowNotion != null) {
                    summary.text = bookRowNotion.summary
                    stars.rating = bookRowNotion.reviewStartNumbers.toFloat()
                    Log.w("[ami]", "${bookRowNotion.genres?.size}")
                    Log.w("[ami]", "${summary.text} was here")
                    val layoutParams = FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT
                    )

                    bookRowNotion.genres.forEach { genre ->
                        createChip(chipGroup, layoutParams, genre ?: "")
                    }
                } else {
                    summary.text =
                        "You didn't read this book or you don't have a summary for it, please add one."
                    stars.rating = 0f
                }
                val childNode = Node()
                childNode.parent = bookNode
                childNode.renderable = viewRenderable
                childNode.localPosition = Vector3(0.0f, 0.25f, 0.0f)
            }
    }

    private fun b(
        it: BookRow,
        book: Book
    ) : Boolean {
        it.title = it.title.removeDiacritics()
        book.title = book.title.removeDiacritics()

        return (it.title.contains(book.title, ignoreCase = true)
                || book.title.contains(it.title, ignoreCase = true))
    }

    private fun CharSequence.removeDiacritics(): String {
        val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()

        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return regex.replace(temp, "")
    }

    private fun createChip(
        chipGroup: ChipGroup,
        layoutParams: FlexboxLayout.LayoutParams,
        genre: String
    ) {
      if (genre.isEmpty()) return
        val gen = Chip(chipGroup.context)

//        gen.layoutParams = layoutParams
        gen.text = genre
        gen.textSize = 5f
        gen.gravity = (Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL)
        gen.setPadding(0, 0, 0, 0)
        gen.chipStartPadding = 0f
        gen.chipEndPadding = 0f
        gen.setEnsureMinTouchTargetSize(false)
        gen.textStartPadding = 10f
        gen.textEndPadding = 0f
        gen.chipMinHeight = 0f
        gen.minHeight = 0
        gen.minimumHeight = 0
        gen.closeIconSize = 0f
        gen.closeIconEndPadding = 0f

        layoutParams.height = 80//ChipGroup.LayoutParams.WRAP_CONTENT
        layoutParams.width =  genre.length * 10
        gen.layoutParams = layoutParams

        chipGroup.addView(gen)
    }


    private fun loadViewForCover(book: Book, bookNode: Node) {
        if (book.cover.NeedDefaultCover)
            ViewRenderable.builder().setView(fragment.context, R.layout.book_template).build()
                .thenAccept { viewRenderable ->
                    createTemplateViewCover(
                        viewRenderable,
                        bookNode,
                        book
                    )
                }
                .exceptionally {
                    return@exceptionally onException(activity, it)
                }
        else ViewRenderable.builder().setView(fragment.context, R.layout.image_cover).build()
            .thenAccept { viewRenderable ->
                createCoverFromImage(
                    book, bookNode, viewRenderable
                )
            }
            .exceptionally {
                return@exceptionally onException(activity, it)
            }
    }
}