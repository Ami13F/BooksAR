package com.example.booksar.helpers

import android.content.Context
import android.util.TypedValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageHelper {
    companion object {
        fun dpToPix(context: Context?, dp: Float): Float =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context!!.resources.displayMetrics
            )
    }
}