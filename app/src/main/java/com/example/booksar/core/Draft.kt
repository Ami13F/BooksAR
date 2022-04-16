package com.example.booksar.core

import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.example.booksar.R
import com.example.booksar.helpers.ImageHelper
import com.example.booksar.models.Book
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class Draft {
    private fun scaleObject(book: Book, bookNode: TransformableNode): TransformableNode {
        val modelSize = Vector3(0.14903799f, 0.038000144f, 0.2450379f)

//        bookNode.scaleController.minScale = 1f
//        bookNode.scaleController.maxScale = 3f
        val child = Node()
        child.localScale = Vector3(
            1f, 1f, 1f
//            book.size.x / modelSize.x,
//            book.size.y / modelSize.y,
//            book.size.z / modelSize.z
        )
        child.localRotation = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 0f)
        child.localPosition = Vector3(book.position.x, book.position.y, book.position.z)
        bookNode.addChild(child)

        return bookNode
    }

    //TODO: use later to display book info
    //this code display a "info about book" if render like createTemplateView
    private fun createInfoPopup(
        viewRenderable: ViewRenderable,
        coverNode: Node,
        bookNode: Node,
        book: Book
    ) {
        coverNode.renderable = viewRenderable

        val box = ((bookNode.renderable as ModelRenderable).collisionShape as Box)
        val modelSize = box.size

        coverNode.localPosition = Vector3(modelSize.x, modelSize.y, modelSize.z)
//        coverNode.localScale =
//            Vector3(0.87f * parentBox.size.x / realXm, 0.87f * parentBox.size.z / realYm, 1f)
        coverNode.localRotation = Quaternion.axisAngle(Vector3(0f, 0.0f, 0.0f), 0f)

    }

    companion object {
        fun paintBookSecondVersion(bookNode: Node) {
            val mat = bookNode.renderableInstance!!.getMaterial("pages_pages_diffuse.tga")
            mat.setFloat4(
                "baseColorFactor",
                242f / 255f, 238f / 255f, 203f / 255f, 1f
            )
            bookNode.renderableInstance!!.setMaterial(0, mat)

            val mat2 = bookNode.renderableInstance!!.getMaterial("cover_cover_diffuse.tga")
            mat.setFloat4(
                "baseColorFactor",
                234f / 255f, 198f / 255f, 78f / 255f, 1f
            )
            bookNode.renderableInstance!!.setMaterial(1, mat2)
        }

        fun displayPopupInfo(fragment: ArFragment, bookNode: Node) {
            //TODO: Think about what to trigger the info popup
            bookNode.setOnTouchListener { hitTestResult, motionEvent ->
                Toast.makeText(fragment.context, "You touched the book", Toast.LENGTH_SHORT).show()
                return@setOnTouchListener true
            }
        }
    }
}