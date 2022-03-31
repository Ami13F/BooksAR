package com.example.booksar.core;

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.booksar.R
import com.example.booksar.SearchActivity
import com.google.android.filament.Colors
import com.google.android.filament.LightManager
import com.google.ar.core.Config
import com.google.ar.sceneform.ux.ArFragment
import com.gorisse.thomas.sceneform.light.LightEstimationConfig
import com.gorisse.thomas.sceneform.light.build
import com.gorisse.thomas.sceneform.lightEstimationConfig
import com.gorisse.thomas.sceneform.mainLight

class BookArFragment : ArFragment(){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableLight()
    }

    private fun disableLight() {
//        arSceneView.mainLight = LightManager.Builder(LightManager.Type.DIRECTIONAL).apply {
//            val (r, g, b) = Colors.cct(5_500.0f)
//            color(r, g, b)
//            intensity(100_000.0f)
//            direction(0.0f, -0.0f, -0.26f)
//            castShadows(true)
//        }.build()
        arSceneView.lightEstimationConfig = LightEstimationConfig(
            Config.LightEstimationMode.DISABLED,
            environmentalHdrReflections = true,
            environmentalHdrSpecularFilter = true
        )
    }
}
