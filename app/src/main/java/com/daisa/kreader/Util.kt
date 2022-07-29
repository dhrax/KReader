package com.daisa.kreader

import android.content.ContentResolver
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import java.text.SimpleDateFormat
import java.util.*

fun getContentValues(): ContentValues {
    val name = SimpleDateFormat(Constants.FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }
    }

    return contentValues
}

fun getoutputOptions(contentResolver: ContentResolver): ImageCapture.OutputFileOptions {
    return ImageCapture.OutputFileOptions
        .Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            getContentValues()
        )
        .build()
}

object Constants{
    const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
}