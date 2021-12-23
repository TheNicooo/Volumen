package com.example.volumen.ui.scandit

import com.example.volumen.utils.KEY_SCANDIT
import com.scandit.datacapture.barcode.capture.BarcodeCapture
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.Camera.Companion.getDefaultCamera
import java.lang.IllegalStateException


class DataCaptureManager private constructor() {
    val barcodeCapture: BarcodeCapture
    val dataCaptureContext: DataCaptureContext = DataCaptureContext.forLicenseKey(KEY_SCANDIT)
    val camera: Camera? = getDefaultCamera(BarcodeCapture.createRecommendedCameraSettings())

    companion object {
        private const val SCANDIT_LICENSE_KEY = KEY_SCANDIT
        val CURRENT = DataCaptureManager()
    }

    init {
        // Create data capture context using your license key and set the camera as the frame source.

        // Use the default camera with the recommended camera settings for the BarcodeCapture mode
        // and set it as the frame source of the context. The camera is off by default and must be
        // turned on to start streaming frames to the data capture context for recognition.
        if (camera != null) {
            dataCaptureContext.setFrameSource(camera)
        } else {
            throw IllegalStateException(
                "Sample depends on a camera, which failed to initialize.")
        }

        // Create new barcode capture mode with default settings. Each mode of the sample will
        // apply its own settings specific to its use case.
        barcodeCapture =
            BarcodeCapture.forDataCaptureContext(dataCaptureContext, BarcodeCaptureSettings())
    }
}