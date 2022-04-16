package com.example.booksar.core

import android.os.Bundle
import android.view.View
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import com.gorisse.thomas.sceneform.light.LightEstimationConfig
import com.gorisse.thomas.sceneform.lightEstimationConfig


class BooksArFragment : ArFragment(){

    lateinit var bookArService: BookArService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableLight()
        sessionConfiguration()
    }

    private fun disableLight() {
        arSceneView.lightEstimationConfig = LightEstimationConfig(
            Config.LightEstimationMode.DISABLED,
            environmentalHdrReflections = true,
            environmentalHdrSpecularFilter = true
        )
    }
    private fun sessionConfiguration() {
        arSceneView.session = Session(this.activity)
        val config = Config(arSceneView.session)
        config.cloudAnchorMode = Config.CloudAnchorMode.ENABLED
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        arSceneView.session!!.configure(config)
    }


}
