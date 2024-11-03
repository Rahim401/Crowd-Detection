package com.crowd.client.ui.pages.mainPage.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crowd.client.R

@Composable
fun Background(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.background),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier.scale(1.05f).blur(4.dp).background(Color.Black),
    )
}

@Preview
@Composable
private fun Preview() {
    Background(Modifier.fillMaxSize(1f))
}