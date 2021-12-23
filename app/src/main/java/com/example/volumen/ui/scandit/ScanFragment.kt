package com.example.volumen.ui.scandit

import android.R
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.scandit.datacapture.barcode.capture.*
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.barcode.data.SymbologyDescription
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.*
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scandit.datacapture.core.ui.style.Brush
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle
import java.lang.IllegalStateException
import java.util.*

class ScanFragment: Fragment(), BarcodeCaptureListener {

    val SCANDIT_LICENSE_KEY =
        "AZ7wGw7vLl5WHbYPoRDq8VAEgAxmNGYcClAGn+NlN5DIZz5SFg42EstfOzcrPybmMnpVeTx3pzb9cpWdeUceWh4n+jeqbn1HGCGXQ+9lqsN8VUaTw1I6rGRasdODZKmKNFjTdtJL0/YHWgo//UWkp9ZOkqqLW45whV8tNxh5u8TiVdEbDU+uWdFJ84mkYkafmCXfw40vwtBqUJuWiFwEO/sIRvwRVUXSRUcCbq9IvfFPZpNciUZd2qBbIIdDQuZUFC8q1kFlNLcGXQreZzfV7pNPhBTBTb4FmGmHIPB/5911cS92MFkNdCdgCVI/YO9fHWQPSLdjNaszbm+GG1YWpRtsLapNW+Zjnlqm5apUjSOIRuieTHvy5CMsojEJbiKAvWXHCoMsT32GVyvAIUeF979AeDqBQejgbXuLGN53PTOFTNC5pHgOCTtTL9kNeeDuwmE0+GBIW6lBBBjZUkAEjjdB65K7cZaKOQht+5dX3WkpRrTben1eApp6CjOjcibL7hneICU+hTcHCZbAPw9viOhVOFYjq/xhaUOUvAx7aWNHwQhJ3rhR/73BufWB+yZX7Xp3tpswhAvgb5W/EaP+4yBbyw3rpTuvxIMur7AicycQoWSAEMNt3x0HGH7v6k4z6YuOtx5WH0DsUqVqKbBkSVuQPxqWGJcD2jDSeWx3n+APcUAnyxQPot+kbRO9o330q4neTWoTyTY6Ql+m4Mn31wp98qaDUxZomqYuOB3tozUXaugEKCUi2i92Wgus136J8B2fi3tXFZ9DtfSnAJwEC59zcEXev27jD408DkbmTCeGqjPOuwSJ4A3gg0+GKXL/VVwMTF9SGveu3E3fijup426EAEHMK66gmdCukhRsSLrOSZ+neUuvloIr2GHeRxEg8rfwDpvNhRGgynXC96s1jZffib0zFosrcAEVfvVVfhTYMQTafIniFbzl+96WUwGBYRthVjZ6Z8eFxxkxzu9MhClMDHRDAJp/g1amS4XN6PC9bF+mi43rgfUTv0PlQ9YlgEnvx97TYEvRo/zqs0lrlphriQY7OF47V/bsnGn9TyPRVITluIe+mJlPC+J7UPw4U1LINLt7ekLhKiJcUvXTYbEgdnFP3Eb9k5s6/NDowww6rFZ+5mCb2vr88M4+sgPmR8tRZlWXrZQ41YwYbGNh24rgY25uD+QY6BykzRmSThDZIJss5R0wp9YfP91MgdwS"

    private var dataCaptureContext: DataCaptureContext? = null
    private var barcodeCaptureSettings: BarcodeCaptureSettings? = null
    private var barcodeCapture: BarcodeCapture? = null

    private val torchState = TorchState.OFF
    private val cameraPosition = CameraPosition.WORLD_FACING
    private val videoResolution = VideoResolution.FULL_HD
    private val camera: Camera? = Camera.getCamera(cameraPosition)
    private val cameraSettings = BarcodeCapture.createRecommendedCameraSettings()

    private var dataCaptureView: DataCaptureView? = null

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize and start the barcode recognition.
        initializeAndStartBarcodeScanning()
    }

    private fun initializeAndStartBarcodeScanning() {
        barcodeCaptureSettings = BarcodeCaptureSettings()
        cameraSettings.preferredResolution = videoResolution
        camera!!.applySettings(cameraSettings)

        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY)

        // Use the default camera with the recommended camera settings for the BarcodeCapture mode
        // and set it as the frame source of the context. The camera is off by default and must be
        // turned on to start streaming frames to the data capture context for recognition.
        // See resumeFrameSource and pauseFrameSource below.
        if (camera != null) {
            dataCaptureContext!!.setFrameSource(camera)
        } else {
            throw IllegalStateException("Sample depends on a camera, which failed to initialize.")
        }

        // The barcode capturing process is configured through barcode capture settings
        // which are then applied to the barcode capture instance that manages barcode recognition.
        // BarcodeCaptureSettings barcodeCaptureSettings = new BarcodeCaptureSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a very generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires as
        // every additional enabled symbology has an impact on processing times.
        val symbologies = HashSet<Symbology>()
        symbologies.add(Symbology.EAN13_UPCA)
        symbologies.add(Symbology.EAN8)
        symbologies.add(Symbology.UPCE)
        symbologies.add(Symbology.QR)
        symbologies.add(Symbology.DATA_MATRIX)
        symbologies.add(Symbology.CODE39)
        symbologies.add(Symbology.CODE128)
        symbologies.add(Symbology.INTERLEAVED_TWO_OF_FIVE)
        barcodeCaptureSettings!!.enableSymbologies(symbologies)
        // Some linear/1d barcode symbologies allow you to encode variable-length data.
        // By default, the Scandit Data Capture SDK only scans barcodes in a certain length range.
        // If your application requires scanning of one of these symbologies, and the length is
        // falling outside the default range, you may need to adjust the "active symbol counts"
        // for this symbology. This is shown in the following few lines of code for one of the
        // variable-length symbologies.
        val symbologySettings = barcodeCaptureSettings!!.getSymbologySettings(Symbology.CODE39)
        val activeSymbolCounts = HashSet(
            Arrays.asList(*arrayOf<Short>(7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)))
        symbologySettings.activeSymbolCounts = activeSymbolCounts

        // Create new barcode capture mode with the settings from above.
        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext,
            barcodeCaptureSettings!!)

        // Register self as a listener to get informed whenever a new barcode got recognized.
        barcodeCapture!!.addListener(this)

        // To visualize the on-going barcode capturing process on screen, setup a data capture view
        // that renders the camera preview. The view must be connected to the data capture context.
        dataCaptureView = DataCaptureView.newInstance(requireContext(), dataCaptureContext)

        // Add a barcode capture overlay to the data capture view to render the location of captured
        // barcodes on top of the video preview.
        // This is optional, but recommended for better visual feedback.
        val overlay = BarcodeCaptureOverlay.newInstance(barcodeCapture!!, dataCaptureView)
        overlay.viewfinder = RectangularViewfinder(RectangularViewfinderStyle.ROUNDED)

        // Adjust the overlay's barcode highlighting to match the new viewfinder styles and improve
        // the visibility of feedback. With 6.10 we will introduce this visual treatment as a new
        // style for the overlay.
        val brush = Brush(Color.TRANSPARENT, Color.BLACK, 3f)
        overlay.brush = brush
        (dataCaptureView)
    }

    override fun onPause() {
        pauseFrameSource()
        super.onPause()
    }

    override fun onDestroy() {
        barcodeCapture!!.removeListener(this)
        dataCaptureContext!!.removeMode(barcodeCapture!!)
        super.onDestroy()
    }

    private fun pauseFrameSource() {
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode capture as well.
        barcodeCapture?.isEnabled = false
        camera!!.switchToDesiredState(FrameSourceState.OFF, null)
    }

    override fun onResume() {
        super.onResume()

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        //requestCameraPermission()
    }

    fun onCameraPermissionGranted() {
        resumeFrameSource()
    }

    private fun resumeFrameSource() {
        dismissScannedCodesDialog()

        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        barcodeCapture?.isEnabled = true
        camera!!.switchToDesiredState(FrameSourceState.ON, null)
    }

    private fun dismissScannedCodesDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }

//    private fun showResult(result: String) {
//        val builder = AlertDialog.Builder(this)
//        dialog = builder.setCancelable(false)
//            .setTitle(result)
//            .setPositiveButton(R.string.ok
//            ) { dialog, which ->
//                // barcodeCapture.setEnabled(true);
//                // Al presionar Aceptar (u OK), se devuelve a la pantalla de eleccion.
//                val intent = Intent(this, ::class.java)
//                intent.putExtra("extra", result)
//                startActivityForResult(intent, 0)
//                //startActivity(intent);
//            }
//            .create()
//        dialog!!.show()
//    }

    override fun onBarcodeScanned(
        barcodeCapture: BarcodeCapture,
        session: BarcodeCaptureSession,
        frameData: FrameData,
    ) {
//        if (session.newlyRecognizedBarcodes.isEmpty()) return
//        val barcode = session.newlyRecognizedBarcodes[0]
//
//        // Stop recognizing barcodes for as long as we are displaying the result. There won't be any
//        // new results until the capture mode is enabled again. Note that disabling the capture mode
//        // does not stop the camera, the camera continues to stream frames until it is turned off.
//        barcodeCapture.setEnabled(false)
//
//        // If you are not disabling barcode capture here and want to continue scanning, consider
//        // setting the codeDuplicateFilter when creating the barcode capture settings to around 500
//        // or even -1 if you do not want codes to be scanned more than once.
//
//        // Get the human readable name of the symbology and assemble the result to be shown.
//        val symbology: String =
//            SymbologyDescription.create(barcode.getSymbology()).getReadableName()
//        // final String result = "Resultado: " + barcode.getData() + " (" + symbology + ")";
//        val format: String = SymbologyDescription.create(barcode.getSymbology()).getReadableName()
//        val result: String = barcode.getData()
//        val intent = Intent(this@BarcodeScanActivity, MainActivity::class.java)
//        intent.putExtra("result", result)
//        intent.putExtra("format", format)
//        setResult(Activity.RESULT_OK, intent)
//        finish()
    }

    override fun onSessionUpdated(
        barcodeCapture: BarcodeCapture,
        session: BarcodeCaptureSession, data: FrameData,
    ) {
    }

    override fun onObservationStarted(barcodeCapture: BarcodeCapture) {}

    override fun onObservationStopped(barcodeCapture: BarcodeCapture) {}

}