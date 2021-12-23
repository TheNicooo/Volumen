package com.example.volumen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.volumen.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val PERMISSION_CODE_ACCEPTED = 1
    private val PERMISSION_CODE_NOT_AVAILABLE = 0

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private var isLocationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

            isReadPermissionGranted = it[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isWritePermissionGranted = it[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
            isLocationPermissionGranted = it[Manifest.permission.ACCESS_FINE_LOCATION] ?: isLocationPermissionGranted

        }

        requestPermission()

    }

    private fun requestPermission() {
        isReadPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        isWritePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        isLocationPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()

        if (!isReadPermissionGranted) permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!isWritePermissionGranted) permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!isLocationPermissionGranted) permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionRequest.isNotEmpty()) permissionLauncher.launch(permissionRequest.toTypedArray())
    }

    private fun requestLocationPermission(): Int {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                // request permission
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_CODE_ACCEPTED)
            }
        } else {
            // already granted
            return PERMISSION_CODE_ACCEPTED
        }

        // not available
        return PERMISSION_CODE_NOT_AVAILABLE
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                //Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
            } else {
                Log.i("Permission: ", "Denied")
                //Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
}