package com.example.volumen.ui.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.volumen.R
import com.example.volumen.api.volumen.CodeRequest
import com.example.volumen.api.volumen.Volumen
import com.example.volumen.databinding.FragmentVolumenBinding
import com.example.volumen.ui.scandit.FullscreenScanActivity
import com.example.volumen.utils.KEY_SCANDIT
import com.example.volumen.utils.Resource
import com.example.volumen.utils.URL_VOLUMEN
import com.example.volumen.utils.formatRut
import com.google.android.material.snackbar.Snackbar
import com.scandit.datacapture.core.capture.DataCaptureContext
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VolumenFragment : Fragment(R.layout.fragment_volumen) {

    private var _binding: FragmentVolumenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VolumenViewModel by viewModels()

    private var ip: String? = ""
    private lateinit var customProgressDialog: Dialog

    private var dataCaptureContext: DataCaptureContext =
        DataCaptureContext.forLicenseKey(KEY_SCANDIT)

    private var flag: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val bundle = arguments
        val code = bundle?.getString("code", "")
        _binding = FragmentVolumenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        setUpView()
        initObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        val bundle = arguments
        val code = bundle?.getString("code", "")
        super.onResume()
    }

    private fun init() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val rut = sharedPref.getString("RUT", "")
        val namePortico = sharedPref.getString("NAME_PORTICO", "")
        ip = sharedPref.getString("IP", "")
        binding.apply {
            tvRutPortico.text = rut?.let { formatRut(it) }
            tvPortico.text = namePortico
        }
        customProgressDialog = Dialog(requireContext())
    }

    private fun setUpView() {
        binding.apply {
            lUser.setOnClickListener {
                findNavController().navigate(R.id.action_volumen_to_login)
            }
            lPortico.setOnClickListener {
                findNavController().navigate(R.id.action_volumen_to_portico)
            }
            lRequestSizes.setOnClickListener {
                val code = CodeRequest("12345")
                val url = ip + URL_VOLUMEN
                viewModel.getVolumen(url, code)
            }
            scanner.setOnClickListener {
                val intent = Intent(requireContext(), FullscreenScanActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initObservers() {
        viewModel.volumenLiveData.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Error -> showErrorView(result.error)
                is Resource.Loading -> showLoadingView()
                is Resource.Success -> showSuccessView(result.data)
            }
        })
    }

    private fun showSuccessView(data: Volumen?) {
        binding.apply {
            data?.info_volumen?.apply {
                edtHigh.setText(alto.toString())
                edtLong.setText(largo.toString())
                edtWidth.setText(ancho.toString())
                edtVolume.setText(volumen.toString())
            }
        }
        customProgressDialog.dismiss()
    }

    private fun showLoadingView() {
        customProgressDialog.setContentView(R.layout.custom_dialog)
        customProgressDialog.setCancelable(false)
        customProgressDialog.show()
    }

    private fun showErrorView(error: Throwable?) {
        binding.apply {
            customProgressDialog.dismiss()
            Snackbar.make(requireView(), error?.message.toString(), Snackbar.LENGTH_LONG).show()
        }
    }

//    private fun scandit() {
//        val settings = BarcodeCaptureSettings()
//        settings.apply {
//            enableSymbology(Symbology.CODE128, true)
//            enableSymbology(Symbology.CODE39, true)
//            enableSymbology(Symbology.QR, true)
//            enableSymbology(Symbology.EAN8, true)
//            enableSymbology(Symbology.UPCE, true)
//            enableSymbology(Symbology.EAN13_UPCA, true)
//        }
//
//        val barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, settings);
//
//        barcodeCapture.addListener(this)
//
//        val cameraSettings = BarcodeCapture.createRecommendedCameraSettings()
//        val camera = Camera.getDefaultCamera()
//        camera?.applySettings(cameraSettings)
//
//        dataCaptureContext.setFrameSource(camera)
//
//        camera?.switchToDesiredState(FrameSourceState.ON)
//
//        val dataCaptureView = DataCaptureView.newInstance(requireContext(), dataCaptureContext)
//
//        //binding.dataCaptureView.dataCaptureContext = dataCaptureContext
//
//        val overlay = BarcodeCaptureOverlay.newInstance(barcodeCapture, dataCaptureView)
//
//
//    }
//
//    override fun onBarcodeScanned(
//        barcodeCapture: BarcodeCapture,
//        session: BarcodeCaptureSession,
//        data: FrameData,
//    ) {
//        val recognizedBarcodes: List<Barcode> = session.newlyRecognizedBarcodes
//        super.onBarcodeScanned(barcodeCapture, session, data)
//    }

//    override fun onDestroyView() {
//        _binding = null
//        super.onDestroyView()
//    }

}