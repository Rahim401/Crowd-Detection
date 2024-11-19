package com.crowd.client.utils.android

import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.core.content.edit
import com.crowd.client.application.MainApplication.AppCompanion
import com.google.gson.Gson
import com.google.gson.GsonBuilder

val GsonInstance: Gson = GsonBuilder().serializeNulls().create()
fun SharedPreferences?.getInt(key: String) = this?.all?.get(key) as? Int
fun SharedPreferences?.getLong(key: String) = this?.all?.get(key) as? Long
fun SharedPreferences?.getFloat(key: String) = this?.all?.get(key) as? Float
fun SharedPreferences?.getBoolean(key: String) = this?.all?.get(key) as? Boolean
fun SharedPreferences?.getString(key: String) = this?.all?.get(key) as? String
@Suppress("UNCHECKED_CAST")
fun SharedPreferences?.getStringSet(key: String) = this?.all?.get(key) as? Set<String>
inline fun <reified T: Enum<T>> SharedPreferences?.getEnum(key: String): T? =
    getInt(key)?.let { T::class.java.enumConstants?.get(it) }
inline fun <reified T> SharedPreferences?.getObject(key: String): T? =
    getString(key)?.let { GsonInstance.fromJson(it, T::class.java) }

fun SharedPreferences?.containsOrNull(key: String) = this?.contains(key)
fun SharedPreferences?.put(key: String, value: Any?, finallyToJson: Boolean = true) =
    this?.edit(true) { put(key, value, finallyToJson) }
fun Editor.put(key: String, value: Any?, finallyToJson: Boolean = true): Editor {
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
        else -> if(finallyToJson) putString(
            key, GsonInstance.toJson(value)
        )
    }
    return this
}

abstract class AppPreferences: AppCompanion {
    abstract val prefName: String

    private var weakPref: WeakReference<SharedPreferences>? = null
    protected val pref get() = weakPref?.get()

    protected open fun onPrefLoaded(pref: SharedPreferences) {
        if(!isPrefComplete(pref))
            initializePref(pref)
    }
    protected open fun isPrefComplete(pref: SharedPreferences) = true
    protected open fun initializePref(pref: SharedPreferences) {}
    override fun initialize(context: Context) {
        val appPref = context.getSharedPreferences(prefName, Application.MODE_PRIVATE)
        onPrefLoaded(appPref)
        weakPref = WeakReference(appPref)
    }
    override fun finish() {
        weakPref?.clear()
    }
}