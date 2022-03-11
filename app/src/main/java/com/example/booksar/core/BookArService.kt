package com.example.booksar.core

import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import com.example.booksar.MainActivity
import com.example.booksar.models.Book
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.RenderableInstance
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

    fun createBookObject(bookUrl: Uri): CompletableFuture<ModelRenderable> {
        return ModelRenderable.builder()
            .setSource(fragment.context, bookUrl)
            .setIsFilamentGltf(true)
            .build()
    }

    private fun displayObject(createAnchor: Anchor, bookUrl: Uri) {
        createBookObject(bookUrl)
            .thenAccept {
                //TODO:replace with book from api
                val book = Book(
                    500,
                    "",
                    size = Vector3(10.14903799f, 10.038000144f, 0.2450379f),
                    position = Vector3(hitPose!!.qx(), hitPose!!.qy(), hitPose!!.qy()),
                    rotation = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 0f)
                )
                addObjectToScene(fragment, createAnchor, book, it)
            }
            .exceptionally {
                val builder = AlertDialog.Builder(activity)
                builder.setMessage(it.message)
                    .setTitle("Error!")
                builder.create().show()

                return@exceptionally null
            }
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

    private fun paintBook(
        node: TransformableNode,
        render: RenderableInstance,
    ): TransformableNode {
        val backgroundColor = com.google.ar.sceneform.rendering.Color(Color.GREEN)
        val blue  = com.google.ar.sceneform.rendering.Color(Color.BLUE)

        val materialInstances = render.filamentAsset!!.materialInstances
        for (materialInstance in materialInstances) {
            when (materialInstance.name) {
                "pages_pages_diffuse.tga" -> {
                    materialInstance.setParameter(
                        "baseColorFactor",
                        255f,255f,255f  //white
                    )
                }
                "binding_binding_diffuse.tga" -> {
                    materialInstance.setParameter(
                        "baseColorFactor",
                        backgroundColor.r,
                        backgroundColor.g,
                        backgroundColor.b
                    )
                }
                "cover_cover_diffuse.tga" -> {
                    materialInstance.setParameter(
                        "baseColorFactor",
                        blue.r,
                        blue.g,
                        blue.b
                    )
                }
            }
        }

        return node
    }

    private fun addObjectToScene(
        fragment: ArFragment,
        anchor: Anchor,
        book: Book,
        model: ModelRenderable
    ) {
        val anchorNode = AnchorNode(anchor)
        anchorNode.parent = fragment.arSceneView.scene

        var bookNode = TransformableNode(fragment.transformationSystem)
        bookNode.parent = anchorNode

        val render = bookNode.setRenderable(model)
        scaleObject(book, bookNode)
        paintBook(bookNode, render)

        bookNode.select()
    }
}