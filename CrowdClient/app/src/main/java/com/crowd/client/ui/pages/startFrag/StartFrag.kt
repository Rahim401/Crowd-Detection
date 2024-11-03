package com.crowd.client.ui.pages.startFrag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crowd.client.ui.pages.common.components.AppButton
import com.crowd.client.ui.pages.mainPage.components.Background
import com.crowd.client.ui.theme.CrowdClientTheme

@Composable
fun StartFrag(modifier: Modifier = Modifier, onEnter: () -> Unit = {}) {
    Column(modifier, Arrangement.SpaceBetween) {
        Text(
            "Crowd Density Estimation",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 100.dp)
        )
        AppButton(
            text = "Enter",
            Modifier.align(Alignment.CenterHorizontally)
                .padding(bottom = 100.dp),
            onClick = onEnter
        )
    }
}

@Preview(backgroundColor = 0xFF000000)
@Composable
private fun Preview() {
    CrowdClientTheme {
        Background(Modifier.fillMaxSize())
        StartFrag(Modifier.fillMaxSize())
    }
}