package com.softdough.grow.util

import android.content.res.Resources
import android.util.DisplayMetrics

object ScreenUtil {
    private val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics

    //Width
    val DP_8 = dpToPx(8).toInt()
    val DP_16 = dpToPx(16).toInt()

    //Height
    val DP_7 = dpToPx(7).toInt()
    val DP_14 = dpToPx(14).toInt()

    fun deviceWidth(): Int = displayMetrics.widthPixels

    fun deviceHeight(): Int = displayMetrics.heightPixels

    fun dpToPx(dp: Int): Float = (dp * displayMetrics.density)

    fun pxToDp(px: Int): Float = (px / displayMetrics.density)

    fun percentToPxWidth(percent: Float): Float =
        dpToPx((displayMetrics.widthPixels / displayMetrics.density).toInt()) * percent

    fun percentToPxHeight(percent: Float): Float =
        (displayMetrics.heightPixels / displayMetrics.density) * percent
}