package com.daisa.kreader.barcodescanner

import android.media.Image
import android.util.Log
import androidx.camera.core.ImageProxy
import com.daisa.kreader.GraphicOverlay
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeScannerProcessor (options: BarcodeScannerOptions){

    private val TAG = "KReader"
    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

    fun detectInImage(image: InputImage): Task<List<Barcode>> {
        return barcodeScanner.process(image)
    }

    fun onSuccess(barcodes: List<Barcode>, graphicOverlay: GraphicOverlay) {
        if (barcodes.isEmpty()) {
            Log.v(TAG, "No barcode has been detected")
        }
        graphicOverlay.clear()

        for (barcode in barcodes) {
            val bounds = barcode.boundingBox
            val corners = barcode.cornerPoints

            val rawValue = barcode.rawValue

            graphicOverlay.add(BarcodeGraphic(graphicOverlay, barcode))

            val valueType = barcode.valueType
            // See API reference for complete list of supported types
            when (valueType) {
                Barcode.TYPE_WIFI -> {
                    val ssid = barcode.wifi!!.ssid
                    val password = barcode.wifi!!.password
                    val type = barcode.wifi!!.encryptionType
                    Log.d(TAG, "Codigo decodificado WIFI: ssid: $ssid, password: $password, type: $type")
                }
                Barcode.TYPE_URL -> {
                    val title = barcode.url!!.title
                    val url = barcode.url!!.url

                    Log.d(TAG, "Codigo decodificado URL: titulo: $title, url: $url")
                }
                Barcode.TYPE_PRODUCT ->{
                    val test = barcode.displayValue
                    Log.d(TAG, "Codigo decodificado PRODUCT: titulo: $test")
                }
                else ->
                    Log.d(TAG, "Se ha detectado un codigo desconocido ${barcode.valueType}")

            }
        }
    }

    fun onFailure(e: Exception) {
        Log.e(TAG, "Barcode detection failed $e")
        Log.d(TAG, "BarcodeScannerProcessor OnFailureListener")
    }

    fun onComplete(mediaImage: Image, image: ImageProxy) {
        mediaImage.close()
        image.close()
    }
}