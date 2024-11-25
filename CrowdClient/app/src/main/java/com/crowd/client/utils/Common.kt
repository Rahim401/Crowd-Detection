package com.crowd.client.utils

import android.annotation.SuppressLint
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Helper to handle date formatting
@SuppressLint("ConstantLocale")
private val timeFormat = SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault())
fun time2Str(time: Date = Date()): String = timeFormat.format(time)
fun timeStamp2Str(time: Long = System.currentTimeMillis()): String = timeFormat.format(Date(time))
fun str2Time(string: String): Date? = timeFormat.parse(string)
fun str2TimeStamp(string: String): Long? = str2Time(string)?.time

@SuppressLint("ConstantLocale")
private val timeOtFormat = SimpleDateFormat("h:mma", Locale.getDefault())
fun time2OtStr(time: Date = Date()): String = timeOtFormat.format(time)
fun timeStamp2OtStr(time: Long = System.currentTimeMillis()): String = timeOtFormat.format(Date(time))

private val timeHtFormat = SimpleDateFormat("ha", Locale.getDefault())
fun time2HtStr(time: Date = Date()): String = timeHtFormat.format(time)
fun timeStamp2HtStr(time: Long = System.currentTimeMillis()): String = timeHtFormat.format(Date(time))

@SuppressLint("ConstantLocale")
private val sTimeFormat = SimpleDateFormat("yyyy-MM-dd' 'h:mma", Locale.getDefault())
fun time2SStr(time: Date = Date()): String = sTimeFormat.format(time)
fun timeStamp2SStr(time: Long = System.currentTimeMillis()): String = sTimeFormat.format(Date(time))

val RootGson = GsonBuilder().serializeNulls().create()

object Colors {
    const val Green = "\u001b[32m"
    const val Red = "\u001b[31m"
    const val White = "\u001b[37m"
    const val Blue = "\u001b[34m"
    const val Orange = "\u001b[33m"

    const val Reset = "\u001b[0m"
//    const val Bold = "\\033[1m"
//    const val Italics = "\\x1B[3m"
}


inline fun <T> nullOnE(block: () -> T): T? {
    return try { block() }
    catch (e: Exception) { null }
}