package com.example.booksar.core

import android.app.Activity
import android.graphics.Point
import android.view.View

class ArScreen() {
    companion object {
        fun getScreenCenter(activity: Activity): Point {
            val view = activity.findViewById<View>(android.R.id.content)
            return Point(view.width / 2, view.height / 2) // find the middle point
        }
    }
}