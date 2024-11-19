package com.crowd.client.utils.android

import android.os.Build
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if(model.startsWith(manufacturer)) model.capitalize(Locale.current)
    else manufacturer.capitalize(Locale.current) + " " + model
}