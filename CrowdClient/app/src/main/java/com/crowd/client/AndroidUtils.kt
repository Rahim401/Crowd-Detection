package com.crowd.client

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.Editor
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale

fun Context.goToLink(link: String) {
    if(link.isBlank()) return
    try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link))) }
    catch (ex: ActivityNotFoundException) { makeToast("Invalid Link!") }
}
fun Context.goToActivity(cls: Class<*>, extras: HashMap<String, String> = hashMapOf()) {
    try {
        startActivity(
            Intent(this, cls).apply {
                extras.forEach { putExtra(it.key, it.value) }
            }
        )
    } catch (ex: ActivityNotFoundException) {
        makeToast("No Activity found!")
    }
}

fun Context.shareMessage(title:String, message:String, selectTitle:String = "Select app to share on") {
    try {
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "textContent/plain"
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    putExtra(Intent.EXTRA_TEXT, message)
                }, selectTitle
            )
        )
    } catch (ex: ActivityNotFoundException) {
        makeToast("No app found to Share!")
    }
}
fun Context.writeGmail(to: Array<String>, subject: String) {
    try {
        startActivity(
            Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, to)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                setPackage("com.google.android.gm")
            }
        )
    } catch (ex: ActivityNotFoundException) {
        makeToast("No Mail App found!")
    }
}
fun Context.copyText(text:String, label:String = "") {
    if(label.isNotEmpty()) makeToast("Error on copy text!")
    val clipMg = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipMg.setPrimaryClip(ClipData.newUri(contentResolver, label, Uri.parse(text)))
}
fun Context.makeToast(message: String, isShort:Boolean = true) = Toast.makeText(
    this, message,
    if(isShort) Toast.LENGTH_SHORT
    else Toast.LENGTH_LONG
).show()
fun Context.makeToast(@StringRes message: Int, isShort:Boolean = true) = Toast.makeText(
    this, message,
    if(isShort) Toast.LENGTH_SHORT
    else Toast.LENGTH_LONG
).show()

fun Editor.putObject(key: String, value: Any?): Editor {
    when(value) {
        is Boolean -> putBoolean(key, value)
        is Int -> putInt(key, value)
        is Float -> putFloat(key, value)
        is Long -> putLong(key, value)
        is String -> putString(key, value)
        is Enum<*> -> putInt(key, value.ordinal)
        is Collection<*> -> putStringSet(
            key, value.filterIsInstance<String>()
                .toSet()
        )
    }
    return this
}

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if(model.startsWith(manufacturer)) model.capitalize(Locale.current)
    else manufacturer.capitalize(Locale.current) + " " + model
}