package com.example.booksar.core

import android.net.Uri
import com.example.booksar.MainActivity
import com.example.booksar.R
import com.example.booksar.helpers.CoverHelper.Companion.createCoverFromImage
import com.example.booksar.helpers.CoverHelper.Companion.createTemplateViewCover
import com.example.booksar.helpers.ExceptionHelper.Companion.onException
import com.example.booksar.models.Book
import com.example.booksar.models.BookCover
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.concurrent.CompletableFuture


class BookArService(private val activity: MainActivity, private var fragment: ArFragment) {

    private var hitPose: Pose? = null

    fun createObject(bookUrl: Uri) {
        disableLight()
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

    private fun disableLight() {
        fragment.arSceneView.session?.apply {
            // Configure the session with the Lighting Estimation API in ENVIRONMENTAL_HDR mode.
            val configLight = config
            configLight.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            configure(config)
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
                val book = createBook(false)
                addObjectToScene(fragment, createAnchor, book, it)
            }
            .exceptionally {
                return@exceptionally onException(activity, it)
            }
    }

    private fun createBook(isTemplate: Boolean = true): Book {
        return Book(
            500,
            size = Vector3(10.14903799f, 10.038000144f, 0.2450379f),
            position = Vector3(hitPose!!.qx(), hitPose!!.qy(), hitPose!!.qy()),
            rotation = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 0f),
            cover = BookCover().apply { NeedDefaultCover = isTemplate }
        )
    }

    private fun scaleObject(book: Book, bookNode: TransformableNode): TransformableNode {
        val modelSize = Vector3(0.14903799f, 0.038000144f, 0.2450379f)
        bookNode.scaleController.maxScale = 5f
        bookNode.scaleController.minScale = 1f

        bookNode.localScale = Vector3(
            book.size.x / modelSize.x,
            book.size.y / modelSize.y,
            book.size.z / modelSize.z
        )
        bookNode.localRotation = book.rotation
        bookNode.localPosition = book.position

        return bookNode
    }

    private fun addObjectToScene(
        fragment: ArFragment,
        anchor: Anchor,
        book: Book,
        model: ModelRenderable
    ) {
        val anchorNode = AnchorNode(anchor)
        anchorNode.parent = fragment.arSceneView.scene

        val bookNode = TransformableNode(fragment.transformationSystem)
        bookNode.parent = anchorNode

        bookNode.renderable = model
        scaleObject(book, bookNode)
        addCover(book, bookNode)

        bookNode.select()
    }

    private fun addCover(book: Book, bookNode: Node) {
        loadViewForCover(book, bookNode)
//        paintBook(bookNode, book.coverColor) //book.cover.spineColor
    }

    private fun loadViewForCover(book: Book, bookNode: Node) {
        val coverNode = Node()

        if (book.cover.NeedDefaultCover)
            ViewRenderable.builder().setView(fragment.context, R.layout.book_template).build()
                .thenAccept { viewRenderable ->
                    createTemplateViewCover(
                        viewRenderable,
                        coverNode,
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
                    book, coverNode, bookNode, viewRenderable
                )
            }
            .exceptionally {
                return@exceptionally onException(activity, it)
            }

        coverNode.parent = bookNode
    }
}