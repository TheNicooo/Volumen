package com.example.volumen.ui.portico

import android.content.Context
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.volumen.R
import com.example.volumen.api.portico.PorticoStatus
import com.example.volumen.databinding.FragmentPorticoBinding
import com.example.volumen.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PorticoFragment : Fragment(R.layout.fragment_portico) {

    private var _binding: FragmentPorticoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PorticoViewModel by viewModels()

    private var ip: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPorticoBinding.inflate(inflater, container, false)
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
        val wifiName = sharedPref.getString("WIFI_NAME", "")
        binding.apply {
            tvRutPortico.text = rut?.let { formatRut(it) }
            tvWifiName.text = wifiName
        }
        getPorticos()
    }

    private fun setUpView() {
        binding.apply {
            lUser.setOnClickListener {
                findNavController().navigate(R.id.action_portico_to_login)
            }
            btnGoToMain.setOnClickListener {
                findNavController().navigate(R.id.action_portico_to_volumen)
            }
        }
    }

    private fun getPorticos() {
        ip = "http://${getIp()}141:5000"
        val ipPortico = ip + URL_PORTICO
        viewModel.getPortico(ipPortico)
    }

    private fun initObservers() {
        viewModel.porticoLiveData.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Error -> showErrorView(result.error)
                is Resource.Loading -> showLoadingView()
                is Resource.Success -> showSuccessView(result.data)
            }
        })
    }

    private fun showSuccessView(data: PorticoStatus?) {
        binding.apply {
            progressBar.gone()
            val status = data?.info_portico?.status
            if (status != null) {
                changeCardView(status)
            }
            saveData()
        }
    }

    private fun changeCardView(status: String) {
        val textView = binding.txtPortico1
        when (status) {
            "maintenance" -> textView.setBackgroundColor(Color.GREEN)
            else -> {
                textView.setBackgroundColor(Color.RED)
                Snackbar.make(requireView(), "Error!!", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun showLoadingView() {
        binding.progressBar.visible()
    }

    private fun showErrorView(error: Throwable?) {
        binding.apply {
            progressBar.gone()
            Snackbar.make(requireView(), error?.message.toString(), Snackbar.LENGTH_LONG).show()
        }
    }

    @Suppress("DEPRECATION")
    private fun getIp(): String {
        var outIp = ""
        val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager!!.connectionInfo
        val ip = Formatter.formatIpAddress(wifiInfo.ipAddress)

        ip.let {
            val part = it.split(".")
            outIp = "${part[0]}.${part[1]}.${part[2]}."
        }

        return outIp

    }

    private fun saveData() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("IP", ip)
            putString("NAME_PORTICO", "Portico 1")
            apply()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}