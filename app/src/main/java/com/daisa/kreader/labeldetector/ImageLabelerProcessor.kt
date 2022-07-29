package com.daisa.kreader.labeldetector

import android.media.Image
import android.util.Log
import androidx.camera.core.ImageProxy
import com.daisa.kreader.GraphicOverlay
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

//todo create Processor interface
class ImageLabelerProcessor(options: ImageLabelerOptions) {

    private val TAG = "KReader"
    val laberer = ImageLabeling.getClient(options)

    fun detectInImage(image: InputImage): Task<List<ImageLabel>> {
        return laberer.process(image)
    }

    fun onSuccess(labels: List<ImageLabel>, graphicOverlay: GraphicOverlay) {
        for (label in labels) {
            val text = label.text
            val confidence = label.confidence
            val index = label.index

            Log.d(TAG, "Laberer data: text: $text, confidence: $confidence, index: $index")
        }
        graphicOverlay.add(LabelGraphic(graphicOverlay, labels))

    }

    fun onFailure(e: Exception) {
        Log.e(TAG, "Image laberer detection failed $e")
    }

    fun onComplete(mediaImage: Image, image: ImageProxy) {
        mediaImage.close()
        image.close()
    }
}