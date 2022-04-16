package com.example.booksar.core

import android.app.Activity
import android.net.Uri
import android.view.MotionEvent.ACTION_UP
import com.example.booksar.R
import com.example.booksar.helpers.CoverHelper.Companion.createCoverFromImage
import com.example.booksar.helpers.CoverHelper.Companion.createTemplateViewCover
import com.example.booksar.helpers.ExceptionHelper.Companion.onException
import com.example.booksar.models.Book
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
import java.util.concurrent.CompletableFuture


class BookArService(private val activity: Activity, private var fragment: ArFragment) {

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
                // TODO: update scaling
                firebaseService.updateBook(motionEvent, bookNode, book.id)
            }
            return@setOnTouchListener true
        }
        bookNode.localRotation =
            Quaternion(book.rotation.x, book.rotation.y, book.rotation.z, book.rotation.w)

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