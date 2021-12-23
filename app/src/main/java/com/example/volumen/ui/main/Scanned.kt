package com.example.volumen.ui.main

interface Scanned {

    var codeScan: String

    fun requestCode(code: String) {
        codeScan = code
    }

    fun showCode() : String {
        return codeScan
    }
}