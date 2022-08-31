package com.daisa.kreader.barcodescanner

import android.content.Intent
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageProxy
import com.daisa.kreader.CodeType
import com.daisa.kreader.Constants
import com.daisa.kreader.GraphicOverlay
import com.daisa.kreader.activity.LinkPreviewActivity
import com.daisa.kreader.activity.WiFiPreviewActivity
import com.daisa.kreader.db.entity.Code
import com.daisa.kreader.db.viewmodel.CodeViewModel
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class BarcodeScannerProcessor(options: BarcodeScannerOptions, val codeViewModel: CodeViewModel) {

    private val TAG = "KReader"
    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)
    public var shouldProcess = true

    fun detectInImage(image: InputImage): Task<List<Barcode>> {
        return barcodeScanner.process(image)
    }

    fun onSuccess(barcodes: List<Barcode>, graphicOverlay: GraphicOverlay) {
        if (shouldProcess) {
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
                Log.d(Constants.TAG, "barccode.displayValue: ${barcode.displayValue}")
                Log.d(Constants.TAG, "barccode.rawValue: ${barcode.rawValue}")
                // See API reference for complete list of supported types
                when (valueType) {
                    Barcode.TYPE_WIFI -> {
                        val ssid = barcode.wifi!!.ssid
                        val password = barcode.wifi!!.password
                        val type = barcode.wifi!!.encryptionType

                        Log.d(
                            TAG,
                            "Codigo decodificado WIFI: ssid: $ssid, password: $password, type: $type"
                        )

                        insertToHistory(barcode.displayValue!!, CodeType.WIFI)

                        val wifiIntent =
                            Intent(graphicOverlay.context, WiFiPreviewActivity::class.java)
                                .apply {
                                    putExtra("ssid", ssid)
                                    putExtra("pwd", password)
                                    putExtra("type", type)
                                }
                        graphicOverlay.context.startActivity(wifiIntent)
                        shouldProcess = false
                    }
                    Barcode.TYPE_URL -> {
                        val title = barcode.url!!.title
                        val url = barcode.url!!.url

                        Log.d(TAG, "Codigo decodificado URL: titulo: $title, url: $url")
                        insertToHistory(barcode.displayValue!!, CodeType.URL)

                        val urlIntent =
                            Intent(graphicOverlay.context, LinkPreviewActivity::class.java)
                                .apply {
                                    putExtra("link", url)
                                }
                        graphicOverlay.context.startActivity(urlIntent)
                        shouldProcess = false
                    }
                    Barcode.TYPE_PRODUCT -> {
                        val displayValue = barcode.displayValue
                        Log.d(TAG, "Codigo decodificado PRODUCT: displayValue: $displayValue")
                        insertToHistory(barcode.displayValue!!, CodeType.PRODUCT)
                    }
                    Barcode.TYPE_TEXT -> {
                        val value = barcode.displayValue
                        Log.d(TAG, "Codigo decodificado TEXT: value: $value")
                        insertToHistory(barcode.displayValue!!, CodeType.TEXT)
                    }
                    else ->
                        Log.d(TAG, "Se ha detectado un codigo desconocido ${barcode.valueType}")

                }
            }
        }
    }

    fun onFailure(e: Exception) {
        Log.e(TAG, "Barcode detection failed $e")
        Log.d(TAG, "BarcodeScannerProcessor OnFailureListener")
    }

    fun onComplete(image: Image, imageProxy: ImageProxy) {
        image.close()
        imageProxy.close()
    }

    private fun insertToHistory(text: String, type: CodeType) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
        val currentDate = sdf.format(Date())
        val code = Code(text = text, favorite = false, date = currentDate.toString(), type = type)

        val dispatcherScope =
            CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())


        dispatcherScope.launch(Dispatchers.IO) {
            Log.d(Constants.TAG, "I'm working in thread ${Thread.currentThread().name}")
            if (codeViewModel.repository.getCodeByText(code.text) == 0) {
                Log.d(Constants.TAG, "Insertando codigo nuevo")
                codeViewModel.repository.insert(code)
            } else {
                Log.d(Constants.TAG, "Codigo ya insertado")
            }
        }


    }
}