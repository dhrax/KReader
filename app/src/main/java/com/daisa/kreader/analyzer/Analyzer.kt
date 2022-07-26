package com.daisa.kreader.analyzer

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.daisa.kreader.GraphicOverlay
import com.daisa.kreader.barcodescanner.BarcodeScannerProcessor
import com.daisa.kreader.labeldetector.ImageLabelerProcessor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class Analyzer(
    graphicOverlay: GraphicOverlay?,
    private val barcodeScannerProcessor: BarcodeScannerProcessor
) : ImageAnalysis.Analyzer {

    private val TAG = "KReader"
    private var graphicOverlay: GraphicOverlay? = null
    var imageLabelerProcessor: ImageLabelerProcessor


    init {
        this.graphicOverlay = graphicOverlay

        val labererOptions = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.9f)
            .build()
        imageLabelerProcessor = ImageLabelerProcessor(labererOptions)
    }

    @ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

            val rotationDegrees = inputImage.rotationDegrees
            if (rotationDegrees == 0 || rotationDegrees == 180) {
                graphicOverlay!!.setImageSourceInfo(inputImage.width, inputImage.height, false)
            } else {
                graphicOverlay!!.setImageSourceInfo(inputImage.height, inputImage.width, false)
            }

            val result = barcodeScannerProcessor.detectInImage(inputImage)
            result
                .addOnSuccessListener { barcodes ->
                    barcodeScannerProcessor.onSuccess(barcodes, graphicOverlay!!)
                }
                .addOnFailureListener { e ->
                    barcodeScannerProcessor.onFailure(e)
                }
                .addOnCompleteListener {
                    barcodeScannerProcessor.onComplete(mediaImage, image)
                }

            /*val labelResult = imageLabelerProcessor.detectInImage(inputImage)

            labelResult
                .addOnSuccessListener { labels ->
                    imageLabelerProcessor.onSuccess(labels, graphicOverlay!!)
                }
                .addOnFailureListener { e ->
                    imageLabelerProcessor.onFailure(e)
                }
                .addOnCompleteListener {
                    //closing the images when this processor finishes to be able to have one barcodeScannerProcessor and one imageLabelerProcessor working at the same time
                    imageLabelerProcessor.onComplete(mediaImage, image)
                }
                */

        }


    }
}