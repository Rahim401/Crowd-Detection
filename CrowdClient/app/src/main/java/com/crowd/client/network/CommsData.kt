package com.crowd.client.network

import com.crowd.client.utils.time2Str
import java.util.Date

data class Location(val place: String, val address: String)
data class PostCrowdData(
    val atLocation: String, val atTime: String = time2Str(Date()),
    val fromMail: String = "DefMail@crowd.com", val message: String = "No Message!",
    val photo: String? = null, val crowdAt: Int = -1
)

//data class CreateLocationResponse(val status: String, val message: String)
//data class GetLocationsResponse(val allLocations: List<String>)
//data class GetEstimationResponse(val estimation: String)
//data class GetPhotoNearResponse(val photos: List<String>)
//data class GetCrowdSeqResponse(val sequence: List<String>)
