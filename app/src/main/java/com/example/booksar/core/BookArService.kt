package com.example.booksar.core

import android.app.AlertDialog
import android.net.Uri
import com.example.booksar.MainActivity
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class BookArService(private val activity: MainActivity, var fragment: ArFragment) {

    fun createObject(bookUrl : Uri){
        val frame = fragment.arSceneView.arFrame
        val point = ArScreen.getScreenCenter(activity)
        if (frame != null){
            val hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits){
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)){
                    displayObject(fragment, hit.createAnchor(), bookUrl)
                }
            }
        }
    }

    private fun displayObject(fragment: ArFragment, createAnchor: Anchor, bookUrl: Uri) {
        ModelRenderable.builder()
            .setSource(fragment.context, bookUrl)
            .setIsFilamentGltf(true)
            .build()
            .thenAccept{
                addObjectToScene(fragment, createAnchor, it)
            }
            .exceptionally {
                val builder = AlertDialog.Builder(activity)
                builder.setMessage(it.message)
                    .setTitle("Error!")
                builder.create().show()

                return@exceptionally null
            }
    }

    private fun addObjectToScene(fragment: ArFragment, anchor: Anchor, modelRender: ModelRenderable) {
        val anchorObject = AnchorNode(anchor)
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = modelRender
        transformableNode.parent = anchorObject
        fragment.arSceneView.scene.addChild(anchorObject)
        transformableNode.select()
    }
}