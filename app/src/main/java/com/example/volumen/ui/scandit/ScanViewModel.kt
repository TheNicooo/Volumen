package com.example.volumen.ui.scandit

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import com.scandit.datacapture.barcode.capture.BarcodeCapture
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.FrameSourceState


class ScanViewModel : ViewModel(), BarcodeCaptureListener {
    private val dataCaptureManager: DataCaptureManager = DataCaptureManager.CURRENT
    private val mainHandler = Handler(Looper.getMainLooper())
    private var listener: ResultListener? = null

    // The barcode capturing process is configured through barcode capture settings
    // which are then applied to the barcode capture instance that manages barcode recognition.
    private val barcodeCaptureSettings: BarcodeCaptureSettings
        private get() {
            // The barcode capturing process is configured through barcode capture settings
            // which are then applied to the barcode capture instance that manages barcode recognition.
            val barcodeCaptureSettings = BarcodeCaptureSettings()

            // The settings instance initially has all types of barcodes (symbologies) disabled.
            // For the purpose of this sample we enable a very generous set of symbologies.
            // In your own app ensure that you only enable the symbologies that your app requires
            // as every additional enabled symbology has an impact on processing times.
            barcodeCaptureSettings.enableSymbology(Symbology.EAN13_UPCA, true)
            barcodeCaptureSettings.enableSymbology(Symbology.EAN8, true)
            barcodeCaptureSettings.enableSymbology(Symbology.UPCE, true)
            barcodeCaptureSettings.enableSymbology(Symbology.QR, true)
            barcodeCaptureSettings.enableSymbology(Symbology.DATA_MATRIX, true)
            barcodeCaptureSettings.enableSymbology(Symbology.CODE39, true)
            barcodeCaptureSettings.enableSymbology(Symbology.CODE128, true)
            barcodeCaptureSettings.enableSymbology(Symbology.INTERLEAVED_TWO_OF_FIVE, true)
            return barcodeCaptureSettings
        }

    val dataCaptureContext: DataCaptureContext
        get() = dataCaptureManager.dataCaptureContext
    val barcodeCapture: BarcodeCapture
        get() = dataCaptureManager.barcodeCapture

    fun setListener(listener: ResultListener?) {
        this.listener = listener
    }

    fun resumeScanning() {
        dataCaptureManager.barcodeCapture.isEnabled = true
    }

    fun pauseScanning() {
        dataCaptureManager.barcodeCapture.isEnabled = false
    }

    fun startFrameSource() {
        if (dataCaptureManager.camera != null) {
            dataCaptureManager.camera.switchToDesiredState(FrameSourceState.ON)
        }
    }

    fun stopFrameSource() {
        if (dataCaptureManager.camera != null) {
            dataCaptureManager.camera.switchToDesiredState(FrameSourceState.OFF)
        }
    }

    override fun onBarcodeScanned(
        barcodeCapture: BarcodeCapture,
        session: BarcodeCaptureSession,
        data: FrameData,
    ) {
        val firstBarcode = session.newlyRecognizedBarcodes[0]
        if (listener != null && firstBarcode != null) {
            // Stop recognizing barcodes for as long as we are displaying the result.
            // There won't be any new results until the capture mode is enabled again.
            // Note that disabling the capture mode does not stop the camera, the camera
            // continues to stream frames until it is turned off.
            pauseScanning()

            // This method is invoked on a non-UI thread, so in order to perform UI work,
            // we have to switch to the main thread.
            mainHandler.post { listener!!.onCodeScanned(firstBarcode) }
        }
    }

    override fun onSessionUpdated(
        barcodeCapture: BarcodeCapture,
        session: BarcodeCaptureSession,
        data: FrameData,
    ) {
    }

    override fun onObservationStarted(barcodeCapture: BarcodeCapture) {}
    override fun onObservationStopped(barcodeCapture: BarcodeCapture) {}
    interface ResultListener {
        @MainThread
        fun onCodeScanned(barcodeResult: Barcode?)
    }

    init {
        dataCaptureManager.barcodeCapture.applySettings(barcodeCaptureSettings)

        // Register self as a listener to get informed whenever a new barcode got recognized.
        dataCaptureManager.barcodeCapture.addListener(this)
    }
}