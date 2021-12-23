package com.example.volumen.ui.scandit

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import com.example.volumen.MainActivity
import com.example.volumen.R
import com.example.volumen.ui.main.Scanned
import com.example.volumen.ui.main.VolumenFragment
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.barcode.data.SymbologyDescription
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scandit.datacapture.core.ui.style.Brush
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue

@AndroidEntryPoint
open class FullscreenScanActivity() : CameraPermissionActivity(), ScanViewModel.ResultListener {
    private val viewModel: ScanViewModel by viewModels()
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // To visualize the on-going barcode capturing process on screen,
        // setup a data capture view that renders the camera preview.
        // The view must be connected to the data capture context.
        val view = DataCaptureView.newInstance(this, viewModel.dataCaptureContext)
        // Add a barcode capture overlay to the data capture view to render the tracked
        // barcodes on top of the video preview.
        // This is optional, but recommended for better visual feedback.
        val overlay = BarcodeCaptureOverlay.newInstance(viewModel.barcodeCapture, view)

        // Adjust the overlay's barcode highlighting to match the new viewfinder styles and improve
        // the visibility of feedback. With 6.10 we will introduce this visual treatment as a new
        // style for the overlay.
        val brush = Brush(Color.TRANSPARENT, Color.WHITE, 3f)
        overlay.brush = brush
        setContentView(view)
    }

    override fun onResume() {
        super.onResume()

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission()
    }

    override fun onCameraPermissionGranted() {
        resumeFrameSource()
    }

    private fun resumeFrameSource() {
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        viewModel.setListener(this)
        viewModel.startFrameSource()
        if (!isShowingDialog) {
            viewModel.resumeScanning()
        }
    }

    private val isShowingDialog: Boolean
        private get() = dialog != null && dialog!!.isShowing

    override fun onPause() {
        super.onPause()
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode tracking as well.
        viewModel.setListener(null)
        viewModel.pauseScanning()
        viewModel.stopFrameSource()
    }

    override fun onCodeScanned(barcodeResult: Barcode?) {
        val message: String = getString(
            R.string.scan_result_parametrised,
            SymbologyDescription.create(barcodeResult!!.symbology),
            barcodeResult?.data,
            barcodeResult?.symbolCount
        )
        val mFragmentManager = supportFragmentManager
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        val mFragment = VolumenFragment()

        val mBundle = Bundle()
        mBundle.putString("code", barcodeResult?.data)
        mFragment.arguments = mBundle
        mFragmentTransaction.replace(R.id.volumen, mFragment).commit()
        //finish()

//        dialog = AlertDialog.Builder(this)
//            .setTitle(getString(R.string.scanned))
//            .setMessage(message)
//            .setCancelable(false)
//            .setPositiveButton(R.string.ok,
//                DialogInterface.OnClickListener { dialog, which -> viewModel.resumeScanning() })
//            .create()
//        dialog!!.show()
    }

    companion object {
        fun getIntent(context: Context?): Intent {
            return Intent(context, FullscreenScanActivity::class.java)
        }
    }
}