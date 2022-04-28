package com.example.booksar.helpers

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.get
import com.example.booksar.R
import com.example.booksar.models.Book
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target


class CoverHelper {
    companion object {
        fun paintBook(
            node: Node,
            coverColor: Int,
        ): Node {
            val hexColor = String.format("#%06X", 0xFFFFFF and coverColor)
//            var test = //Color.pack(coverColor)
//            Color.convert(coverColor, ColorSpace.get(ColorSpace.Named.SRGB))
//            val test = Color.valueOf(coverColor)
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
            bookNode: Node,
            book: Book
        ) {
            val coverNode = Node()
            viewRenderable.renderPriority = 0
            coverNode.parent = bookNode
            coverNode.renderable = viewRenderable
            resizeCover(coverNode, viewRenderable, bookNode)
//            resizeCover2(coverNode, bookNode)
            coverNode.renderable = viewRenderable
            val root = (coverNode.renderable as ViewRenderable).view as FrameLayout
            root.findViewById<TextView>(R.id.author)?.text = book.author
            root.findViewById<TextView>(R.id.title)?.text = book.title
//            root.findViewById<TextView>(R.id.title)?.setTextColor(book.cover.textColor)
//            root.findViewById<TextView>(R.id.author)?.setTextColor(book.cover.textColor)
        }

        fun createCoverFromImage(
            book: Book,
            bookNode: Node,
            viewRenderable: ViewRenderable
        ) {
            val coverNode = Node()
            viewRenderable.renderPriority = 0
            coverNode.parent = bookNode
            coverNode.renderable = viewRenderable

            val img = (coverNode.renderable as ViewRenderable).view as ImageView
            Picasso.get().load(book.cover.url)
                .resize(100, 120)
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                        img.setImageBitmap(bitmap)
                        book.coverColor = bitmap[0, 0]// ColorArt(bitmap).detailColor
                        resizeCover2(coverNode, bookNode)
                        paintBook(bookNode, book.coverColor)
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        TODO("Not yet implemented")
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                })
        }

        private fun resizeCover(
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
            bookNode: Node
        ) {

            val scale = 0.74f
            coverNode.localScale = Vector3(scale - 0.04f, scale, scale)//bookNode.localScale
            coverNode.localPosition = Vector3(
                bookNode.localPosition.x,//- 0.2f,
                bookNode.localPosition.y - 0.065f,
                0.015f
            )
        }
    }
}