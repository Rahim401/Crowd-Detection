package com.crowd.client.viewmodel

import androidx.annotation.DrawableRes

data class PicOfPlace(
    @DrawableRes val image: Int,
    val picDescription: String
)

data class EstResultState(
    val picData: PicOfPlace,
    val crowdIs: String = "low",
    val timeToGo: String = "5:30pm",
    val leastCrowdAt: String = "9am",
)
