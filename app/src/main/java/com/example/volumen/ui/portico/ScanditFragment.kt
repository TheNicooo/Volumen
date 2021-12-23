package com.example.volumen.ui.portico

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.volumen.R
import com.example.volumen.databinding.FragmentScanditBinding
import com.example.volumen.utils.KEY_SCANDIT
import com.google.android.material.snackbar.Snackbar
import com.scandit.datacapture.barcode.capture.BarcodeCapture
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.core.ui.DataCaptureView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanditFragment: Fragment(R.layout.fragment_scandit), BarcodeCaptureListener {

    private var _binding: FragmentScanditBinding? = null
    private val binding get() = _binding!!

    private var dataCaptureContext: DataCaptureContext =
        DataCaptureContext.forLicenseKey(KEY_SCANDIT)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        scandit()
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_scandit_to_volumen)
        }
    }

    private fun scandit() {
        val settings = BarcodeCaptureSettings()
        settings.apply {
            enableSymbology(Symbology.CODE128, true)
            enableSymbology(Symbology.CODE39, true)
            enableSymbology(Symbology.QR, true)
            enableSymbology(Symbology.EAN8, true)
            enableSymbology(Symbology.UPCE, true)
            enableSymbology(Symbology.EAN13_UPCA, true)
        }

        val barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, settings);

        barcodeCapture.addListener(this)

        val cameraSettings = BarcodeCapture.createRecommendedCameraSettings()
        val camera = Camera.getDefaultCamera()
        camera?.applySettings(cameraSettings)

        dataCaptureContext.setFrameSource(camera)

        camera?.switchToDesiredState(FrameSourceState.ON)

        val dataCaptureView = DataCaptureView.newInstance(requireContext(), dataCaptureContext)

        binding.lScandit.addView(dataCaptureView)

        val overlay = BarcodeCaptureOverlay.newInstance(barcodeCapture, dataCaptureView)
    }

    override fun onBarcodeScanned(
        barcodeCapture: BarcodeCapture,
        session: BarcodeCaptureSession,
        data: FrameData,
    ) {
        val recognizedBarcodes = session.newlyRecognizedBarcodes[0]
        val code = recognizedBarcodes.data
        Snackbar.make(requireView(), code!!, Snackbar.LENGTH_LONG).show()
        saveData(code!!)
    }


    private fun saveData(code: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("CODE_SCANDIT", code)
            apply()
        }
    }

    override fun onDestroy() {
        val cameraSettings = BarcodeCapture.createRecommendedCameraSettings()
        val camera = Camera.getDefaultCamera()
        camera?.applySettings(cameraSettings)
        dataCaptureContext.setFrameSource(camera)

        camera?.switchToDesiredState(FrameSourceState.OFF)
        _binding = null
        super.onDestroy()
    }

}