package com.crowd.client.ui.pages.startFrag

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crowd.client.R
import com.crowd.client.ui.pages.common.components.AppButton
import com.crowd.client.ui.pages.mainPage.components.Background
import com.crowd.client.ui.theme.CrowdClientTheme

@Composable
fun LoadingFrag(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    isSuccessful: Boolean = false,
    onGoBack: () -> Unit = {}
) {
    Box(
        modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        if(isLoading) CircularProgressIndicator(
            strokeWidth = 20.dp,
            color = MaterialTheme.colorScheme.secondary,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.size(150.dp)
        )
        else {
            val imageId = if (isSuccessful) R.drawable.sucess
            else R.drawable.failure
            val text = if (isSuccessful)
                "Crowd at Location \nis Estimated"
            else "No details on Location\n to Estimate Crowd"
            val message = if (isSuccessful)
                "Please wait for details!"
            else "Please try again with other location!"

            Image(
                painter = painterResource(id = imageId),
                contentDescription = "Estimation Successful",
                modifier = Modifier
                    .padding(bottom = 250.dp)
                    .size(225.dp)
                    .align(Alignment.Center)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 150.dp),
                Arrangement.spacedBy(10.dp),
                Alignment.CenterHorizontally
            ) {
                Text(
                    text = text, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.85f),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                        .copy(0.7f),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }
            if(!isSuccessful) AppButton(
                text = "Go Back",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 150.dp),
                onClick = onGoBack
            )
        }
    }
}

@Preview(backgroundColor = 0xFF000000)
@Composable
private fun Preview() {
    CrowdClientTheme {
        Background(Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize()) {
            LoadingFrag(
                Modifier.fillMaxSize(), true,
                true
            )
        }
    }
}