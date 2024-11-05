package com.crowd.client.ui.pages.mainPage

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.crowd.client.EnterApp
import com.crowd.client.EstResultState
import com.crowd.client.Fragment
import com.crowd.client.GetEstimation
import com.crowd.client.GoBack
import com.crowd.client.MainVM
import com.crowd.client.SaveUser
import com.crowd.client.UiAction
import com.crowd.client.ui.pages.mainPage.components.Background
import com.crowd.client.ui.pages.resultFrag.EstimationFrag
import com.crowd.client.ui.pages.startFrag.LoadingFrag
import com.crowd.client.ui.pages.startFrag.QueryFrag
import com.crowd.client.ui.pages.startFrag.StartFrag
import com.crowd.client.ui.pages.startFrag.UserFrag
import com.crowd.client.ui.theme.CrowdClientTheme

@Composable
fun MainPage(
    modifier: Modifier = Modifier,
    onFragment: Fragment = Fragment.UserFrag,
    isWaitingForResult: Boolean = true,
    isEstimationSuccessful: Boolean = false,
    estimationResult: EstResultState? = null,
    onAction: (UiAction) -> Unit = {}
) {
    Box(modifier) {
        Background(Modifier.fillMaxSize())
        AnimatedContent(
            targetState = onFragment, label = "",
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        ) { onFrag ->
            Box(Modifier.fillMaxSize()) {
                val formFragMod = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .wrapContentHeight()
                when (onFrag) {
                    Fragment.StartFrag -> StartFrag(Modifier.fillMaxSize()) {
                        onAction(EnterApp)
                    }

                    Fragment.UserFrag -> UserFrag(formFragMod) { name, mail ->
                        onAction(SaveUser(name, mail))
                    }

                    Fragment.QueryFrag -> QueryFrag(formFragMod) { place, date, time ->
                        onAction(GetEstimation(place, date, time))
                    }

                    Fragment.LoadingFrag -> LoadingFrag(
                        Modifier.fillMaxSize(), isWaitingForResult,
                        isEstimationSuccessful
                    ) { onAction(GoBack) }

                    Fragment.ResultPage -> if(estimationResult != null)
                        EstimationFrag(
                            Modifier.fillMaxSize(), estimationResult.picData,
                            estimationResult.crowdIs, estimationResult.timeToGo,
                            estimationResult.leastCrowdAt
                        ) { onAction(GoBack) }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val context = LocalContext.current
    val viewModel = MainVM()
    viewModel.initializeModel(context)

    CrowdClientTheme {
        MainPage(
            Modifier.fillMaxSize(), viewModel.onFragment,
            viewModel.isWaitingForResult,
            viewModel.isEstimationSuccessful,
            viewModel.estimationResult,
            onAction = { viewModel.handelAction(it, context) }
        )
    }
}