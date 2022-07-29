package com.daisa.kreader

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException

class CustomImageSavedCallback(baseContext: Context) : ImageCapture.OnImageSavedCallback {

    private val TAG = "KReader"
    private val baseContext: Context

    init {
        this.baseContext = baseContext
    }

    override fun onError(exc: ImageCaptureException) {
        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
    }

    override fun onImageSaved(output: ImageCapture.OutputFileResults){
        val msg = "Photo capture succeeded: ${output.savedUri}"
        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        Log.d(TAG, msg)
    }
}