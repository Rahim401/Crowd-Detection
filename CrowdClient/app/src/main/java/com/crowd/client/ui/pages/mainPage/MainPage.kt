package com.crowd.client.ui.pages.mainPage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.crowd.client.ui.pages.mainPage.components.Background
import com.crowd.client.ui.pages.startFrag.StartFrag
import com.crowd.client.ui.pages.startFrag.UserFrag
import com.crowd.client.ui.theme.CrowdClientTheme

@Composable
fun MainPage(modifier: Modifier = Modifier) {
    Box(modifier) {
        Background(Modifier.fillMaxSize())
//        StartFrag(Modifier.fillMaxSize())
        UserFrag(
            Modifier.imePadding().fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
//                .windowInsetsPadding()
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CrowdClientTheme {
        MainPage()
    }
}