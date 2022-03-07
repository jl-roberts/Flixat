package com.jlroberts.flixat.utils

import android.app.Activity
import android.view.WindowManager

fun Activity.useClearStatusBar(enabled: Boolean) {
    val params = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    if (enabled) {
        this.window.setFlags(
            params, params
        )
    } else {
        this.window.clearFlags(params)
    }
}