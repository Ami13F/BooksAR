package com.example.booksar.core

import android.net.Uri
import com.example.booksar.MainActivity
import com.example.booksar.R
import com.example.booksar.helpers.CoverHelper.Companion.createCoverFromImage
import com.example.booksar.helpers.CoverHelper.Companion.createTemplateViewCover
import com.example.booksar.helpers.ExceptionHelper.Companion.onException
import com.example.booksar.models.Book
import com.example.booksar.models.Book.Companion.createBook
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.concurrent.CompletableFuture


class BookArService(private val activity: MainActivity, private var fragment: ArFragment) {

    private var hitPose: Pose? = null

    fun createObject(bookUrl: Uri) {
        val frame = fragment.arSceneView.arFrame
        val point = ArScreen.getScreenCenter(activity)
        if (frame != null) {
            val hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    hitPose = hit.hitPose
                    displayObject(hit.createAnchor(), bookUrl)
                }
            }
        }
    }

    private fun createBookObject(bookUrl: Uri): CompletableFuture<ModelRenderable> {
        return ModelRenderable.builder()
            .setSource(fragment.context, bookUrl)
            .setIsFilamentGltf(true)
            .build()
    }

    private fun displayObject(createAnchor: Anchor, bookUrl: Uri) {
        createBookObject(bookUrl)
            .thenAccept {
                //TODO:replace with book from api
                val book = createBook(false, qx= hitPose!!.qx(),qy= hitPose!!.qy(),qz= hitPose!!.qy())
                addObjectToScene(fragment, createAnchor, book, it)
            }
            .exceptionally {
                return@exceptionally onException(activity, it)
            }
    }

    private fun scaleObject(book: Book, bookNode: TransformableNode): TransformableNode {
        val modelSize = Vector3(0.14903799f, 0.038000144f, 0.2450379f)

//        bookNode.scaleController.minScale = 1f
//        bookNode.scaleController.maxScale = 3f
        var child = Node()
        child .localScale = Vector3(1f,1f,1f
//            book.size.x / modelSize.x,
//            book.size.y / modelSize.y,
//            book.size.z / modelSize.z
        )
        child .localRotation = book.rotation
        child .localPosition = book.position
        bookNode.addChild(child)

        return bookNode
    }

    private fun addObjectToScene(
        fragment: ArFragment,
        anchor: Anchor,
        book: Book,
        model: ModelRenderable
    ) {
        val anchorNode = AnchorNode(anchor)

        val bookNode = TransformableNode(fragment.transformationSystem)

        bookNode.localPosition = Vector3(0.0f, 0f,0.0f)
        bookNode.localScale = Vector3(2f, 2f, 2f)
        bookNode.parent = anchorNode
        anchorNode.parent = fragment.arSceneView.scene

        val bookNode2 = Node()
        bookNode2.parent = bookNode
        bookNode2.renderable = model
        bookNode2.localScale = Vector3(2f, 2f, 2f)
//        scaleObject(book, bookNode)

        loadViewForCover(book, bookNode2)

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