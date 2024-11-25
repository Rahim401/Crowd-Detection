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
import com.crowd.client.viewmodel.EnterApp
import com.crowd.client.viewmodel.Fragment
import com.crowd.client.viewmodel.GetEstimation
import com.crowd.client.viewmodel.GoBack
import com.crowd.client.viewmodel.MainVM
import com.crowd.client.viewmodel.SaveUser
import com.crowd.client.viewmodel.UiAction
import com.crowd.client.application.User
import com.crowd.client.ui.pages.mainPage.components.Background
import com.crowd.client.ui.pages.resultFrag.EstimationFrag
import com.crowd.client.ui.pages.startFrag.LoadingFrag
import com.crowd.client.ui.pages.startFrag.QueryFrag
import com.crowd.client.ui.pages.startFrag.StartFrag
import com.crowd.client.ui.pages.startFrag.UserFrag
import com.crowd.client.ui.theme.CrowdClientTheme
import com.crowd.client.viewmodel.EstFailed
import com.crowd.client.viewmodel.EstResult
import com.crowd.client.viewmodel.EstSuccess
import com.crowd.client.viewmodel.Query

@Composable
fun MainPage(
    modifier: Modifier = Modifier,
    onFragment: Fragment = Fragment.UserFrag,
    isWaitingForResult: Boolean = true,
    estimationResult: EstResult = EstFailed(),
    initialQuery: Query = Query.fromTime(),
    availableLocations: List<String> = listOf(),
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
                        val user = User(name, mail)
                        onAction(SaveUser(user))
                    }

                    Fragment.QueryFrag -> QueryFrag(
                        formFragMod, initialQuery, availableLocations
                    ) { query -> onAction(GetEstimation(query)) }

                    Fragment.LoadingFrag -> LoadingFrag(
                        Modifier.fillMaxSize(), isWaitingForResult,
                        estimationResult
                    ) { onAction(GoBack) }

                    Fragment.ResultPage -> EstimationFrag(
                        Modifier.fillMaxSize(), estimationResult as?
                                EstSuccess ?: EstSuccess()
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
    viewModel.initialize(context)

    CrowdClientTheme {
        MainPage(
            Modifier.fillMaxSize(), viewModel.onFragment,
            viewModel.isWaitingForResult, viewModel.estimationResult,
            onAction = { viewModel.handelAction(it, context) }
        )
    }
}