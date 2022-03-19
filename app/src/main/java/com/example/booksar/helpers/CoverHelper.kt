package com.example.booksar.helpers

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.booksar.R
import com.example.booksar.models.Book
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.michaelevans.colorart.library.ColorArt

class CoverHelper {
    companion object {
        fun paintBook(
            node: Node,
            coverColor: Int,
        ): Node {
            val hexColor = String.format("#%06X", 0xFFFFFF and coverColor)

            val render = node.renderableInstance
            val materialInstances = render.filamentAsset!!.materialInstances
            for (materialInstance in materialInstances) {
                when (materialInstance.name) {
                    "pages_pages_diffuse.tga" -> {
                        materialInstance.setParameter(
                            "baseColorFactor",
                            242f / 255f, 238f / 255f, 203f / 255f, 1f
                        )
                    }
                    "binding_binding_diffuse.tga" -> {
                        materialInstance.setParameter(
                            "baseColorFactor",
                            Color.red(coverColor).toFloat() / 255f,
                            Color.green(coverColor).toFloat() / 255f,
                            Color.blue(coverColor).toFloat() / 255f,
                            Color.alpha(coverColor).toFloat() / 255f,
                        )
                    }
                    "cover_cover_diffuse.tga" -> {
                        materialInstance.setParameter(
                            "baseColorFactor",
                            Color.red(coverColor).toFloat() / 255f,
                            Color.green(coverColor).toFloat() / 255f,
                            Color.blue(coverColor).toFloat() / 255f,
                            Color.alpha(coverColor).toFloat() / 255f,
                        )
                    }
                }
            }

            return node
        }

        fun createTemplateViewCover(
            viewRenderable: ViewRenderable,
            coverNode: Node,
            bookNode: Node,
            book: Book
        ) {
//            resizeCover(coverNode, viewRenderable, bookNode)
            coverNode.renderable = viewRenderable
            val root = (coverNode.renderable as ViewRenderable).view as FrameLayout
            root.findViewById<TextView>(R.id.author)?.text = book.author
            root.findViewById<TextView>(R.id.title)?.text = book.title
            root.findViewById<TextView>(R.id.title)?.setTextColor(book.cover.textColor)
            root.findViewById<TextView>(R.id.author)?.setTextColor(book.cover.textColor)
        }

        fun createCoverFromImage(
            book: Book,
            coverNode: Node,
            bookNode: Node,
            viewRenderable: ViewRenderable
        ) {
            coverNode.renderable = viewRenderable
            val img = (coverNode.renderable as ViewRenderable).view as ImageView
            Picasso.get().load(book.cover.url)
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                        img.setImageBitmap(bitmap)
                        book.coverColor = ColorArt(bitmap).backgroundColor
//                    resizeCover2(coverNode, viewRenderable, bookNode)
                        paintBook(bookNode, book.coverColor) //book.cover.spineColor
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        TODO("Not yet implemented")
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                })

            fun resizeCover(
                coverNode: Node,
                viewRenderable: ViewRenderable,
                bookNode: Node
            ) {
                coverNode.renderable = viewRenderable
                val modelSize = (bookNode.collisionShape as Box).size
                // TODO: don't hardcode
                coverNode.localPosition = Vector3(0.0f, -0.06f, 0.025f - modelSize.z / 2)
                coverNode.localScale = Vector3(0.275f, 0.22f, 0.1f)
                coverNode.localRotation = Quaternion.axisAngle(Vector3(0f, 0.0f, 0.0f), 0f)
            }

            fun resizeCover2(
                coverNode: Node,
                viewRenderable: ViewRenderable,
                bookNode: Node
            ) {
                coverNode.renderable = viewRenderable
                val modelSize = (bookNode.collisionShape as Box).size
                // TODO: don't hardcode
                coverNode.localPosition = Vector3(0.0f, -0.08f, 0.025f - modelSize.z / 2)
                coverNode.localScale = Vector3(0.065f, 0.072f, 0.1f)
                coverNode.localRotation = Quaternion.axisAngle(Vector3(0f, 0.0f, 0.0f), 0f)
            }
        }
    }
}