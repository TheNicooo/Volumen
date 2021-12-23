package com.example.volumen.ui.main

class DataScanned(scanned: Scanned): Scanned by scanned {

    fun codeScan(): String {
        return showCode()
    }

}