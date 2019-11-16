package com.ono.inssagram.presentation.indicator

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import com.ono.inssagram.R

class Dot(context: Context, highlighting: Boolean = false) :
    View(context) {

    val dotDefaultResource: Int = R.drawable.ic_indicator_default
    val dotHighlightResource: Int = R.drawable.ic_indicator_highlight


    val dotNormal: Drawable? by lazy { ContextCompat.getDrawable(context, dotDefaultResource) }
    val dotHighLight: Drawable? by lazy { ContextCompat.getDrawable(context, dotHighlightResource) }

    init {
        setState(highlighting)
    }

    fun setState(highlighting: Boolean = false) {
        if (highlighting)
            setBackground(dotHighLight)
        else
            setBackground(dotNormal)
    }
}