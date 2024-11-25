package com.crowd.client.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.crowd.client.utils.RootGson
import retrofit2.Response
import java.io.File


inline fun Response<Map<String, Any?>>.handle(
    onSuccess: (Int, String, Map<String, Any?>) -> Unit,
    onError: (Int, String, Map<String, Any?>) -> Unit = { _, _, _ ->},
    onErrorHandling: (Int, Exception) -> Unit = { _, _ -> }
): Response<Map<String, Any?>> {
    try {
        if(isSuccessful) {
            val resData = body() ?: mapOf()
            val message = ((resData as? Map<*, *>)?.get("message") as? String)
                ?: "Can't Get Message!"
            onSuccess(code(), message, resData)
        }
        else {
            @Suppress("UNCHECKED_CAST")
            val errorData = errorBody()?.string()?.let {
                RootGson.fromJson(it, Map::class.java)
                    as? Map<String, Any?>
            } ?: mapOf<String, Any>()
            val errorMessage = errorData["error"] as? String
                ?: "Can't Get Error Message!"
            onError(code(), errorMessage, errorData)
        }
    }
    catch (e: Exception) { onErrorHandling(code(), e) }
    return this
}

inline fun Response<Map<String, Any?>>.handle(
    onResponse: (Int, String, Map<String, Any?>) -> Unit = { _, _, _ ->},
    onErrorHandling: (Int, Exception) -> Unit = { _, _ -> }
) = handle(onResponse, onResponse, onErrorHandling)


fun getServerAddress(context: Context): String {
    val obbDir = context.obbDir
    val fileName = "ServerIp.txt"
    val file = File(obbDir, fileName)

    if(!obbDir.exists()) obbDir.mkdirs()
    if(!file.exists()) {
        file.createNewFile()
        file.writeText("0.0.0.0")
    }
    return file.readText()
}

fun base642Image(encoded: String): Bitmap? {
    val imageBytes = Base64.decode(encoded, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

val AreasInBangalore = setOf(
    "Cantonment", "Domlur", "Indiranagar", "Rajajinagar", "Malleswaram",
    "Pete", "Sadashivanagar", "Seshadripuram", "Shivajinagar", "Ulsoor",
//    # "Vasanth Nagar", "R. T. Nagar", "Bellandur", "CV Raman Nagar", "Hoodi",
//    # "Krishnarajapuram", "Mahadevapura", "Marathahalli", "Varthur", "Whitefield",
    "Banaswadi", "HBR Layout", "Horamavu", "Kalyan Nagar", "Kammanahalli",
//    # "Lingarajapuram", "Ramamurthy Nagar", "Hebbal", "Jalahalli", "Mathikere",
//    # "Peenya", "Vidyaranyapura", "Yelahanka", "Yeshwanthpur", "Bommanahalli", "Bommasandra",
//    # "BTM Layout", "Electronic City", "HSR Layout", "Koramangala", "Madiwala", "Banashankari",
//    # "Basavanagudi", "Girinagar", "J. P. Nagar", "Jayanagar", "Kumaraswamy Layout", "Padmanabhanagar",
//    # "Uttarahalli", "Anjanapura", "Arekere", "Begur", "Gottigere", "Hulimavu", "Kothnur",
//    "Basaveshwaranagar", "Kamakshipalya", "Kengeri", "Mahalakshmi Layout", "Nagarbhavi",
//    "Nandini Layout", "Nayandahalli", "Rajajinagar", "Rajarajeshwari Nagar", "Vijayanagar",
//    "Devanahalli", "Hoskote", "Bidadi", "Bannerghatta", "Hosur"
)

