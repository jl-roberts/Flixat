package com.jlroberts.flixat.utils

import android.app.Activity
import android.view.WindowManager

fun Activity.useClearStatusBar(enabled: Boolean) {
    val flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
    if (enabled) {
        window.addFlags(flags)
    } else {
        window.clearFlags(flags)
    }
}