package com.example.volumen.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.volumen.BuildConfig
import com.example.volumen.R
import com.example.volumen.databinding.FragmentLoginBinding
import com.example.volumen.utils.ERROR_RUT_1
import com.example.volumen.utils.ERROR_RUT_2
import com.example.volumen.utils.VERSION_NAME
import com.example.volumen.utils.validaRut
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        setUpView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        val versionName = BuildConfig.VERSION_NAME
        binding.apply {
            tvVersion.text = "$VERSION_NAME $versionName"
        }
    }

    private fun setUpView() {
        binding.apply {
            btnLogin.setOnClickListener(View.OnClickListener {

                val rut = edtRut.text.trim().toString()

                if (rut.isEmpty()) {
                    edtRut.error = ERROR_RUT_1
                    edtRut.requestFocus()
                    return@OnClickListener
                } else {
                    if (validaRut(rut) == false) {
                        edtRut.error = ERROR_RUT_2
                        edtRut.requestFocus()
                        return@OnClickListener
                    } else {
                        saveRut()
                        findNavController().navigate(R.id.action_login_to_connectInternetFragment)
                    }
                }
            })
        }

    }

    private fun saveRut() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("RUT", binding.edtRut.text.toString())
            apply()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}