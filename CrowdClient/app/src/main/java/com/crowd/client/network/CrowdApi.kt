package com.crowd.client.network

import android.content.Context
import com.crowd.client.application.MainApplication
import com.crowd.client.utils.nullOnE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object CrowdApi: MainApplication.AppCompanion {
//    private var serverAddress = "0.0.0.0"
    //    private val serverUrl get() = "http://$serverAddress:5000"
//    private const val DefURL = "http://0.0.0.0:5000"
    //    private var retrofit: Retrofit? = null
//    private var httpClient: OkHttpClient? = null
    private var api: CrowdApiSchema? = null
    val coScope = CoroutineScope(Dispatchers.IO)
    var availableLocations: List<String>? = null; private set
    fun isServerReachable() = availableLocations != null
    fun checkIsServerReachable() {
        if(!isServerReachable()) coScope.launch {
            getLocations().await()?.handle(
                onResponse = { _, _, d ->
                    availableLocations = d["allLocations"] as? List<String>
                }
            )
        }
    }

    override fun initialize(context: Context) {
        super.initialize(context)
        initialize(getServerAddress(context))
    }
    fun initialize(serverAddress: String) {
        val serverUrl = "http://$serverAddress:5000"
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(serverUrl).client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(CrowdApiSchema::class.java)
        checkIsServerReachable()
        println(serverUrl)
    }
    override fun finish() {
        super.finish()
        availableLocations = null
    }


    suspend fun getLocationInServer(): List<String> {
        getLocations().await()?.handle(
            onResponse = { _, _, data ->
                @Suppress("UNCHECKED_CAST")
                availableLocations = data["allLocations"] as? List<String>
                    ?: listOf()
                return availableLocations ?: listOf()
            },
            onErrorHandling = { _, _ -> }
        )
        return listOf()
    }


//    fun <T> asyncNOnError(block: () -> T) = coroutineScope.async {
////        block()
//        nullOrError(block = block)
//    }

    // /getLocations (GET)
    suspend fun getLocations() = coScope.async { nullOnE { api?.getLocations() } }
//    fun getLocations(
//        onResponse: (Int, String, Map<String, Any?>) -> Unit = { _, _, _ ->},
//        onErrorHandling: (Int, Exception) -> Unit = { _, _ -> }
//    ) = coroutineScope.async { getLocations()?.handle(onResponse, onErrorHandling) }

    // /createLocation (POST)
    suspend fun createLocation(place: String, address: String) =
        coScope.async { nullOnE { api?.createLocation(Location(place, address)) } }
//    fun createLocation(
//        place: String, address: String,
//        onResponse: (Int, String, Map<String, Any?>) -> Unit = { _, _, _ ->},
//        onErrorHandling: (Int, Exception) -> Unit = { _, _ -> }
//    ) = coroutineScope.launch { createLocation(place, address).handle(onResponse, onErrorHandling) }

    // /postCrowdAt (POST)
    suspend fun postCrowdAt(
        atLocation: String, atTime: String, fromMail: String = "DefMail@crowd. com",
        message: String = "No Message!", photoPath: String? = null, crowdAt: Int = -1
    ) = coScope.async {
        nullOnE {
            api?.postCrowdAt(PostCrowdData(
                atLocation, atTime, fromMail,
                message, photoPath, crowdAt
            ))
        }
    }
//    fun postCrowdAt(
//        atLocation: String, atTime: String, fromMail: String = "DefMail@crowd. com",
//        message: String = "No Message!", photoPath: String? = null, crowdAt: Int = -1,
//        onResponse: (Int, String, Map<String, Any?>) -> Unit = { _, _, _ ->},
//        onErrorHandling: (Int, Exception) -> Unit = { _, _ -> }
//    ) = coroutineScope.launch {
//        postCrowdAt(atLocation, atTime, fromMail, message, photoPath, crowdAt)
//            .handle(onResponse, onErrorHandling)
//    }

    // /getEstimation (GET)
    suspend fun getEstimation(atLocation: String, atTime: String) =
        coScope.async { nullOnE { api?.getEstimation(atLocation, atTime) } }
//    fun getEstimation(
//        atLocation: String, atTime: String,
//        onResponse: (Int, String, Map<String, Any?>) -> Unit = { _, _, _ ->},
//        onErrorHandling: (Int, Exception) -> Unit = { _, _ -> }
//    ) = coroutineScope.launch { getEstimation(atLocation, atTime).handle(onResponse, onErrorHandling) }


    // /getPhotoNear (GET)
    suspend fun getPhotoNear(atLocation: String, atTime: String, recordWith: String = "PhotoOnly") =
        coScope.async { nullOnE { api?.getPhotoNear(atLocation, atTime, recordWith) } }
//    fun getPhotoNear(
//        atLocation: String, atTime: String, recordWith: String = "PhotoOnly",
//        onResponse: (Int, String, Map<String, Any?>) -> Unit = { _, _, _ ->},
//        onErrorHandling: (Int, Exception) -> Unit = { _, _ -> }
//    ) = coroutineScope.launch {
//        getPhotoNear(atLocation, atTime, recordWith)
//            .handle(onResponse, onErrorHandling)
//    }


    // /getCrowdSeq (GET)
    suspend fun getCrowdSeq(atLocation: String, atTime: String, noOfSeq: Int = 4) =
        coScope.async { nullOnE { api?.getCrowdSeq(atLocation, atTime, noOfSeq) } }
//    fun getCrowdSeq(
//        atLocation: String, atTime: String, noOfSeq: Int = 4,
//        onResponse: (Int, String, Map<String, Any?>) -> Unit = { _, _, _ ->},
//        onErrorHandling: (Int, Exception) -> Unit = { _, _ -> }
//    ) = coroutineScope.launch {
//        getCrowdSeq(atLocation, atTime, noOfSeq)
//            .handle(onResponse, onErrorHandling)
//    }
}


