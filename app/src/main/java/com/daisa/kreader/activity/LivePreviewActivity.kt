package com.daisa.kreader.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.daisa.kreader.CustomImageSavedCallback
import com.daisa.kreader.GraphicOverlay
import com.daisa.kreader.analyzer.Analyzer
import com.daisa.kreader.barcodescanner.BarcodeScannerProcessor
import com.daisa.kreader.databinding.ActivityMainBinding
import com.daisa.kreader.getoutputOptions
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LivePreviewActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    //todo implement video capture
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    private var graphicOverlay: GraphicOverlay? = null
    private var imageAnalyzer: ImageAnalysis? = null

    var barcodeScannerProcessor: BarcodeScannerProcessor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        graphicOverlay = viewBinding.graphicOverlay

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        val barcodeOptions = BarcodeScannerOptions.Builder().build()
        barcodeScannerProcessor = BarcodeScannerProcessor(barcodeOptions)

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        //This will be null If we tap the photo button before image capture is set up.
        val imageCapture = imageCapture ?: return

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            getoutputOptions(contentResolver),
            ContextCompat.getMainExecutor(this),
            CustomImageSavedCallback(this)
        )
    }

    private fun captureVideo() {}

    private fun startCamera() {
        //This is used to bind the lifecycle of cameras to the lifecycle owner. This eliminates the task of opening and closing the camera since CameraX is lifecycle-aware.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        //This returns an Executor that runs on the main thread.
        cameraProviderFuture.addListener({
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            buildImageAnalyzer()

            Log.d(TAG, "backpressure strategy  value: ${imageAnalyzer?.backpressureStrategy}")

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeScannerProcessor!!.shouldProcess = true
    }

    companion object {
        private const val TAG = "KReader"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    private fun buildImageAnalyzer(){
        val builder = ImageAnalysis.Builder()
        imageAnalyzer = builder.build()

        imageAnalyzer?.setAnalyzer(
            cameraExecutor, Analyzer(graphicOverlay, barcodeScannerProcessor!!)
        )

    }
}
