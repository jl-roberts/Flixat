package com.jlroberts.flixat.ui.preferences

sealed class Theme() {
    object ThemeLight: Theme()
    object ThemeDark: Theme()
    object ThemeSystem: Theme()
}