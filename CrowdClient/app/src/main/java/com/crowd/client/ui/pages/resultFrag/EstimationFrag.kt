package com.crowd.client.ui.pages.resultFrag

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crowd.client.R
import com.crowd.client.ui.pages.common.components.AppButton
import com.crowd.client.ui.pages.mainPage.components.Background
import com.crowd.client.ui.theme.CrowdClientTheme

data class PicOfPlace(
    @DrawableRes val image: Int,
    val picDescription: String
)

@Composable
fun EstimationFrag(
    modifier: Modifier = Modifier,
    picData: PicOfPlace = PicOfPlace(R.drawable.t1, "PES Canteen at 10pm"),
    crowdIs: String = "low",
    timeToGo: String = "5:30pm",
    leastCrowdAt: String = "9am",
    onGoBack: () -> Unit = {}
) {
    Box(
        modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(10.dp, 50.dp, 10.dp, 10.dp),
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Estimated Result",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 30.dp)
            )

            Text(
                text = "According to our estimations, Crowd at\nthe given location is $crowdIs",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 30.dp)
            )

            Image(
                painter = painterResource(id = picData.image),
                contentDescription = "Place At time",
                modifier = Modifier.padding(top = 30.dp)
                    .clip(ShapeDefaults.Small)
            )
            Text(
                text = picData.picDescription,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 7.dp)
            )

            val message = if(timeToGo.isBlank())
                "It is a good choice to go now!"
            else "We recommend you to go at $timeToGo"
            Text(
                text = message,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 50.dp)
            )

            Text(
                text = "$leastCrowdAt is the time when this place will\n have the least crowd!",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 25.dp)
            )
        }


        AppButton(
            text = "Go Back",
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .fillMaxWidth(0.7f)
                .height(60.dp),
            onClick = onGoBack
        )
    }
}

@Preview(backgroundColor = 0xFF000000)
@Composable
private fun Preview() {
    CrowdClientTheme {
        Background(Modifier.fillMaxSize())
//        Box(Modifier.fillMaxSize()) {
//            EstimationFrag(
//                Modifier.fillMaxSize(),
//            )
//        }
        EstimationFrag(
            Modifier.fillMaxSize(),
        )
    }
}