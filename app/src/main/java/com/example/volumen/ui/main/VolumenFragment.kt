package com.example.volumen.ui.main

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.volumen.R
import com.example.volumen.api.volumen.CodeRequest
import com.example.volumen.api.volumen.Volumen
import com.example.volumen.databinding.FragmentVolumenBinding
import com.example.volumen.ui.scandit.FullscreenScanActivity
import com.example.volumen.utils.*
import com.google.android.material.snackbar.Snackbar
import com.scandit.datacapture.barcode.capture.BarcodeCapture
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.core.ui.DataCaptureView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import com.example.volumen.ui.main.Scanned as Scanned1

@AndroidEntryPoint
class VolumenFragment : Fragment(R.layout.fragment_volumen), BarcodeCaptureListener {

    private var _binding: FragmentVolumenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VolumenViewModel by viewModels()

    private var ip: String? = ""
    private lateinit var customProgressDialog: Dialog
    private var codeScandit: String? = ""

    private var dataCaptureContext: DataCaptureContext =
        DataCaptureContext.forLicenseKey(KEY_SCANDIT)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
//        val bundle = arguments
//        val code = bundle?.getString("code", "")
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
//        val bundle = arguments
//        val code = bundle?.getString("code", "")
        val scan = FullscreenScanActivity()
        val any = scan.code
        super.onResume()
    }

    private fun init() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val rut = sharedPref.getString("RUT", "")
        val namePortico = sharedPref.getString("NAME_PORTICO", "")
        codeScandit = sharedPref.getString("CODE_SCANDIT", "")
        ip = sharedPref.getString("IP", "")
        binding.apply {
            tvRutPortico.text = rut?.let { formatRut(it) }
            tvPortico.text = namePortico
            edtBarcode.setText(codeScandit)
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
                if (codeScandit != null) {
                    val code = CodeRequest(codeScandit!!)
                    val url = ip + URL_VOLUMEN
                    viewModel.getVolumen(url, code)
                }
            }
            scanner.setOnClickListener {
                findNavController().navigate(R.id.action_volumen_to_scandit)
            }
            btnSend.setOnClickListener {
                if (validateSize()) {
                    Snackbar.make(requireView(), "DATOS ENVIADOS", Snackbar.LENGTH_LONG).show()
                    clear()
                } else {
                    Snackbar.make(requireView(), "FAVOR INGRESAR DATOS", Snackbar.LENGTH_LONG).show()
                }
            }
            btnSendNull.setOnClickListener {
                if (validateSize()) {
                    Snackbar.make(requireView(), "DATOS ENVIADOS", Snackbar.LENGTH_LONG).show()
                    clear()
                } else {
                    Snackbar.make(requireView(), "FAVOR INGRESAR DATOS", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun validateSize(): Boolean {
        if (binding.edtBarcode.text.equals("")
            || binding.edtHigh.text.equals("")
            || binding.edtLong.text.equals("")
            || binding.edtWidth.text.equals("")
            || binding.edtVolume.text.equals("")) {
            return false
        }

        return true
    }

    private fun clear() {
        binding.apply {
            edtBarcode.setText("")
            edtWidth.setText("")
            edtHigh.setText("")
            edtLong.setText("")
            edtVolume.setText("")
            imgResult.setImageResource(android.R.color.transparent)
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
        val image = data?.imagen
        val pathImage = ip + URL_IMAGEN + image
        Glide.with(requireContext()).load(pathImage).into(binding.imgResult)
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}