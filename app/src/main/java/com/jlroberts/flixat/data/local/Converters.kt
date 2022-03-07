package com.jlroberts.flixat.data.local

import androidx.room.TypeConverter
import com.jlroberts.flixat.domain.model.Image

class Converters {

    @TypeConverter
    fun fromImage(image: Image?): String? {
        return image?.url
    }

    @TypeConverter
    fun toImage(url: String?): Image? {
        return url?.let { Image(it) }
    }
}