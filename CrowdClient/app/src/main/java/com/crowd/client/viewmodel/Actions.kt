package com.crowd.client.viewmodel

import com.crowd.client.application.User

sealed class UiAction

data object EnterApp: UiAction()
data class SaveUser(val user: User): UiAction()
data class GetEstimation(
    val location: String,
    val atDate: Long?,
    val atTime: Pair<Int, Int>?
): UiAction()
data object GoBack: UiAction()