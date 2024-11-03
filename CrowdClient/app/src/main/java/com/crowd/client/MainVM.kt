package com.crowd.client

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

enum class Fragment {
    StartFrag, UserFrag, QueryFrag,
    LoadingFrag
}

sealed class UiAction
data object EnterApp: UiAction()
data class SaveUser(
    val userName: String,
    val userMail: String
): UiAction()
data class GetEstimation(
    val location: String,
    val atDate: Long?,
    val atTime: Pair<Int, Int>?
//        val atTime: Calendar
): UiAction()
data object GoBack: UiAction()


class MainVM: ViewModel() {
    fun initializeModel(context: Context) {

    }

    val coScope = CoroutineScope(viewModelScope.coroutineContext)
    var onFragment by mutableStateOf(Fragment.StartFrag); private set
    var isWaitingForResult by mutableStateOf(true); private set
    var isEstimationSuccessful by mutableStateOf(false); private set


    fun handelAction(action: UiAction, context: Context?) {
//        println("\nonAction Called($action) at ${System.currentTimeMillis()}")
        when(action) {
            is EnterApp -> if(onFragment == Fragment.StartFrag)
                onFragment = Fragment.UserFrag
            is SaveUser -> if(onFragment == Fragment.UserFrag) when {
                action.userName.isBlank() -> context?.makeToast("Name is Blank!")
                action.userMail.isBlank() -> context?.makeToast("Email is Blank!")
                else -> {
                    // Save user
                    onFragment = Fragment.QueryFrag
                }
            }
            is GetEstimation -> if(onFragment == Fragment.QueryFrag) when {
                action.location.isBlank() -> context?.makeToast("Location is Blank!")
                action.atDate == null -> context?.makeToast("Enter the Date!")
                action.atTime == null -> context?.makeToast("Enter the Time!")
                else -> {
                    isWaitingForResult = true
                    onFragment = Fragment.LoadingFrag
                    val queryDateTime = Calendar.getInstance()
                    val queryDate = Date(action.atDate)
                    queryDateTime.set(
                        queryDate.year, queryDate.month, queryDate.day,
                        action.atTime.first, action.atTime.second, 0
                    )

                    // Send of queryDateTime to server and onResult
                    coScope.launch {
                        delay(2000)
                        isEstimationSuccessful = (System.currentTimeMillis() % 2) == 0L
                        isWaitingForResult = false
                    }
                }
            }
            is GoBack -> if(onFragment == Fragment.LoadingFrag)
                onFragment = Fragment.QueryFrag
        }
    }

}