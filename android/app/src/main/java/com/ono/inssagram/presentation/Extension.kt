package com.ono.inssagram.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Activity.bindColor(@ColorRes res: Int): Lazy<Int> = lazy {
    ContextCompat.getColor(this, res)
}

inline fun <reified T : Activity> Activity.startActivity(bundle: Bundle? = null) {
    startActivity(Intent(this, T::class.java), bundle)
}

inline fun <reified T : Activity> Activity.startActivityWithFinish(bundle: Bundle? = null) {
    startActivity<T>(bundle)
    finish()
}
