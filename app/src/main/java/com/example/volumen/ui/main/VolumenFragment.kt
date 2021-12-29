package com.example.volumen.ui.main

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.volumen.R
import com.example.volumen.api.volumen.CodeRequest
import com.example.volumen.api.volumen.Volumen
import com.example.volumen.databinding.FragmentVolumenBinding
import com.example.volumen.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VolumenFragment : Fragment(R.layout.fragment_volumen) {

    private var _binding: FragmentVolumenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VolumenViewModel by viewModels()

    private var ip: String? = ""
    private lateinit var customProgressDialog: Dialog
    private var codeScandit: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.portico)
        }
        _binding = FragmentVolumenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        setUpView()
        initObservers()
        super.onViewCreated(view, savedInstanceState)
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
                    view?.makeSnackbar(DATA_SENT, true)
                    clear()
                } else {
                    view?.makeSnackbar(DATA_NOT_SENT, false)
                }

            }
            btnSendNull.setOnClickListener {
                if (binding.edtBarcode.text.trim().isNotEmpty()) {
                    view?.makeSnackbar(DATA_SENT, true)
                    clear()
                } else {
                    view?.makeSnackbar(DATA_NOT_SENT, false)
                }
            }
        }
    }

    private fun validateSize(): Boolean {

        binding.apply {
            if (edtBarcode.text.trim().isEmpty()
                || edtVolume.text.trim().isEmpty()
                || edtWidth.text.trim().isEmpty()
                || edtLong.text.trim().isEmpty()
                || edtHigh.text.trim().isEmpty()) {
                return false
            }
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
            view?.makeSnackbar(error?.message.toString(), false)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}