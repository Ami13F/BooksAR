package com.example.booksar.core

import android.graphics.Point
import android.view.View
import com.example.booksar.MainActivity
import com.example.booksar.R
import com.google.android.material.internal.ContextUtils.getActivity


class ArScreen() {
    companion object {
        /*
         Get the middle point of the screen
         TODO: replace with the place where user pressed
         */
        fun getScreenCenter(activity: MainActivity): Point {
            val view = activity.findViewById<View>(android.R.id.content)
            return Point(view.width / 2, view.height / 2) // find the middle point
        }
    }
}