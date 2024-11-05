package com.crowd.client

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crowd.client.ui.pages.resultFrag.PicOfPlace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

enum class Fragment {
    StartFrag, UserFrag, QueryFrag,
    LoadingFrag, ResultPage
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
): UiAction()
data object GoBack: UiAction()

data class EstResultState(
    val picData: PicOfPlace,
    val crowdIs: String = "low",
    val timeToGo: String = "5:30pm",
    val leastCrowdAt: String = "9am",
)

class MainVM: ViewModel() {
    fun initializeModel(context: Context) {

    }

    val crowdDenseList = listOf("Low", "Medium", "High", "Very High")
    val crowdPicList = listOf(R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4)
    val coScope = CoroutineScope(viewModelScope.coroutineContext)
    var onFragment by mutableStateOf(Fragment.StartFrag); private set
    var isWaitingForResult by mutableStateOf(true); private set
    var isEstimationSuccessful by mutableStateOf(false); private set
    var estimationResult: EstResultState? by mutableStateOf(null); private set


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
                        isEstimationSuccessful = action.location == "PES Canteen"
                        isWaitingForResult = false

                        if(isEstimationSuccessful) {
                            delay(1500)
                            val qrHr = action.atTime.first % 4
                            val crowdPic = crowdPicList[qrHr]
                            val picMessage = "${action.location} at ${action.atTime.first%12}am"
                            val crowdDens = crowdDenseList[qrHr]
                            val timeToGo = if(qrHr == 0) "" else {
                                val time = action.atTime.first + (4 - qrHr)
                                val aPm = if(time < 12) "am" else "pm"
                                "${time%12}$aPm"
                            }

                            estimationResult = EstResultState(
                                PicOfPlace(crowdPic, picMessage),
                                crowdDens, timeToGo, "2pm"
                            )
                            onFragment = Fragment.ResultPage
                        }
                    }
                }
            }
            is GoBack -> //if(onFragment == Fragment.LoadingFrag)
                onFragment = Fragment.QueryFrag
        }
    }

}