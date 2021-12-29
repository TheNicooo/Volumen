package com.example.volumen.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.example.volumen.R
import com.google.android.material.snackbar.Snackbar


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.makeSnackbar(message: String, type: Boolean, duration: Int = Snackbar.LENGTH_SHORT) {
    if (type) {
        Snackbar.make(this, message.toUpperCase(), duration)
            .setBackgroundTint(Color.parseColor("#10B425"))
            .setActionTextColor(Color.WHITE)
            .show()
    } else {
        Snackbar.make(this, message.toUpperCase(), duration)
            .setBackgroundTint(Color.parseColor("#E30101"))
            .setActionTextColor(Color.WHITE)
            .show()
    }
}
