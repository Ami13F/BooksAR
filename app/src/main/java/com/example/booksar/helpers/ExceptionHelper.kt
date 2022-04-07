package com.example.booksar.helpers

import android.app.Activity
import android.app.AlertDialog
import com.example.booksar.MainActivity

class ExceptionHelper {

    companion object {
        fun onException(activity: Activity, it: Throwable): Nothing? {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(it.message)
                .setTitle("Error!")
            builder.create().show()

            return null
        }
    }
}