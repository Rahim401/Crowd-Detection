package com.crowd.client.ui.pages.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crowd.client.ui.theme.CrowdClientTheme

@Composable
fun AppButton(
    text: String,
    modifier: Modifier = Modifier,
    textPadding: PaddingValues = PaddingValues(45.dp, 3.dp),
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        shape = ShapeDefaults.Small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = modifier//.wrapContentSize()
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(textPadding)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CrowdClientTheme {
        Column(Modifier, Arrangement.spacedBy(10.dp), Alignment.CenterHorizontally) {
            AppButton("Enter")
            AppButton("Save My Details")
            AppButton("Get Estimation")
        }
    }
}