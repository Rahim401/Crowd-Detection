package com.crowd.client.network

import retrofit2.Response
import retrofit2.http.*

interface CrowdApiSchema {
    // GET /getLocations
    @GET("/getLocations")
    suspend fun getLocations(): Response<Map<String, Any?>>

    // POST /createLocation
    @POST("/createLocation")
    suspend fun createLocation(@Body data: Location): Response<Map<String, Any?>>

    // POST /postCrowdAt
    @POST("/postCrowdAt")
    suspend fun postCrowdAt(@Body data: PostCrowdData):Response<Map<String, Any?>>

    // GET /getEstimation
    @GET("/getEstimation")
    suspend fun getEstimation(
        @Query("atLocation") atLocation: String,
        @Query("atTime") atTime: String
    ): Response<Map<String, Any?>>

    // GET /getPhotoNear
    @GET("/getPhotoNear")
    suspend fun getPhotoNear(
        @Query("atLocation") atLocation: String,
        @Query("atTime") atTime: String,
        @Query("recordWith") recordWith: String
    ): Response<Map<String, Any?>>

    // GET /getCrowdSeq
    @GET("/getCrowdSeq")
    suspend fun getCrowdSeq(
        @Query("atLocation") atLocation: String,
        @Query("atTime") atTime: String,
        @Query("noOfSeq") noOfSeq: Int
    ): Response<Map<String, Any?>>
}

