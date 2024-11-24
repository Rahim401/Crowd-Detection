package com.crowd.client.network

import com.crowd.client.utils.Colors
import com.crowd.client.utils.timeStamp2Str
import kotlinx.coroutines.runBlocking
import com.crowd.client.network.CrowdApi.getLocationInServer

object ApiTest {
    // Test /getLocation (GET)
    suspend fun testGetLocation() {
        println("\n${Colors.Blue}Testing /createLocation${Colors.Reset}")
        CrowdApi.getLocations().await()?.logApi(
            "getLocation",
            "On Proper", expCode = 200
        )
    }

    // Test /createLocation (POST)
    suspend fun testCreateLocation() {
        println("${Colors.Blue}\nTesting /getLocations${Colors.Reset}")
        val areasInServer = getLocationInServer().toSet()
        val areasNotInServer = (AreasInBangalore - areasInServer).toList()

        if(areasInServer.isNotEmpty()) {
            val place = areasInServer.random()
            val address = "Chuma"
            CrowdApi.createLocation(place, address).await()?.logApi(
                endpoint = "/getLocation", title = "On Duplicate Insert",
                expCode = 482, others = mapOf(
                    "Place" to place,
                    "Address" to address
                )
            )
        }
        if(areasNotInServer.isNotEmpty()) {
            val place = areasNotInServer.random()
            val address = "Chuma"
            CrowdApi.createLocation(place, address).await()?.logApi(
                endpoint = "/getLocation", title = "On Proper Insert",
                expCode = 201, others = mapOf(
                    "Place" to place,
                    "Address" to address
                )
            )
        }
        CrowdApi.createLocation("", "Ommbu").await()?.logApi(
            endpoint = "/getLocation", "On No PlaceName",
            expCode = 400, others = mapOf(
                "Place" to "",
                "Address" to "Ommbu"
            )
        )
        CrowdApi.createLocation("Oklahoma", "").await()?.logApi(
            endpoint = "/getLocation", "On No Address",
            expCode = 400, others = mapOf(
                "Place" to "Oklahoma",
                "Address" to ""
            )
        )
    }

    // Skipped /postCrowdAt
    // Skipped /getEstimation

    // Test /getCrowdSeq (GET)
    suspend fun testGetCrowdSeq() {
        println("${Colors.Blue}\nTesting /getCrowdSeq${Colors.Reset}")
        val areasInServer = getLocationInServer().toSet()
        val areasNotInServer = (AreasInBangalore - areasInServer).toList()
        var place = areasInServer.random()
        var atTime = timeStamp2Str()

        if(areasInServer.isNotEmpty()) {
            CrowdApi.getCrowdSeq(place, atTime, 10).await()?.logApi(
                title="On Proper Inputs", expCodes= listOf(200, 206, 222),
                endpoint = "/getCrowdSeq", others = mapOf(
                    "Place" to place, "AtTime" to atTime
                )
            )
            atTime = timeStamp2Str(System.currentTimeMillis()-500000)
            CrowdApi.getCrowdSeq(place, atTime, 3).await()?.logApi(
                title="On Proper Inputs With old data", expCodes= listOf(200, 206, 222),
                endpoint = "/getCrowdSeq", others = mapOf(
                    "Place" to place, "AtTime" to atTime
                )
            )
            atTime = ""
            CrowdApi.getCrowdSeq(place, atTime, 10).await()?.logApi(
                title="On No Time", expCode=400,
                endpoint = "/getCrowdSeq", others = mapOf(
                    "Place" to place, "AtTime" to atTime
                )
            )
        }
        if(areasNotInServer.isNotEmpty()) {
            place = areasInServer.random()
            atTime = timeStamp2Str()
            CrowdApi.getCrowdSeq(place, atTime).await()?.logApi(
                title="On Invalid Location", expCode=404,
                endpoint = "/getCrowdSeq", others = mapOf(
                    "Place" to place, "AtTime" to atTime
                )
            )
        }
        atTime = timeStamp2Str()
        CrowdApi.getCrowdSeq("", atTime).await()?.logApi(
            title="On No Location", expCode=400,
            endpoint = "/getCrowdSeq", others = mapOf(
                "Place" to "", "AtTime" to atTime
            )
        )
    }

    // Test /getGetPhotoNear (GET)
    suspend fun testGetPhotoNear() {
        println("${Colors.Blue}\nTesting /getGetPhotoNear${Colors.Reset}")
        val areasInServer = getLocationInServer().toSet()
        val areasNotInServer = (AreasInBangalore - areasInServer).toList()
        var place = areasInServer.random(); var atTime = timeStamp2Str()
        var recordWith = "PhotoOnly"

        if(areasInServer.isNotEmpty()) {
            CrowdApi.getPhotoNear(place, atTime).await()?.logApi(
                title="On Proper Inputs", expCodes= listOf(200, 206, 222),
                endpoint = "/getGetPhotoNear", others = mapOf(
                    "Place" to place, "AtTime" to atTime
                )
            )
            atTime = timeStamp2Str(System.currentTimeMillis()-500000)
            CrowdApi.getPhotoNear(place, atTime).await()?.logApi(
                title="On Proper Inputs With old data", expCodes= listOf(200, 206, 222),
                endpoint = "/getGetPhotoNear", others = mapOf(
                    "Place" to place, "AtTime" to atTime
                )
            )
            atTime = timeStamp2Str()
            CrowdApi.getPhotoNear(place, atTime, recordWith).await()?.logApi(
                title="With Photo only", expCodes= listOf(200, 222),
                endpoint = "/getGetPhotoNear", others = mapOf(
                    "Place" to place, "AtTime" to atTime,
                    "recordWith" to recordWith
                )
            )
            atTime = timeStamp2Str()
            recordWith = "NoPhotoOnly"
            CrowdApi.getPhotoNear(place, atTime, recordWith).await()?.logApi(
                title="With NoPhoto only", expCodes=listOf(206, 222),
                endpoint = "/getGetPhotoNear", others = mapOf(
                    "Place" to place, "AtTime" to atTime,
                    "recordWith" to recordWith
                )
            )
            atTime = ""
            CrowdApi.getPhotoNear(place, atTime).await()?.logApi(
                title="On No Time", expCode=400,
                endpoint = "/getGetPhotoNear", others = mapOf(
                    "Place" to place, "AtTime" to atTime
                )
            )
        }
        if(areasNotInServer.isNotEmpty()) {
            place = areasNotInServer.random()
            atTime = timeStamp2Str()
            CrowdApi.getPhotoNear(place, atTime).await()?.logApi(
                title="On Invalid Location", expCode=404,
                endpoint = "/getGetPhotoNear", others = mapOf(
                    "Place" to place, "AtTime" to atTime
                )
            )
        }
        place = ""
        atTime = timeStamp2Str()
        CrowdApi.getPhotoNear(place, atTime).await()?.logApi(
            title="On No Location", expCode=400,
            endpoint = "/getGetPhotoNear", others = mapOf(
                "Place" to place, "AtTime" to atTime
            )
        )
    }

    suspend fun testAll() {
        testGetLocation()
        testCreateLocation()
        testGetPhotoNear()
        testGetCrowdSeq()
    }
}

fun main() {
    runBlocking {
//        ApiTest.testGetLocation()
//        ApiTest.testCreateLocation()
//        ApiTest.testGetCrowdSeq()
        ApiTest.testAll()
//        val reqData = PostCrowdData("Domlur", crowdAt = 10)
//        val response = api.postCrowdAt(reqData)
//        response.await()?.logApi(expCode = 200)
//        response.handle(
//            onSuccess = { code, message, data ->
//                println("Success($code): $message")
//                println("Data: $data")
//            },
//            onError = { code, error, data ->
//                println("Error($code): $error")
//                println("Data: $data")
//            },
//        )
    }
}