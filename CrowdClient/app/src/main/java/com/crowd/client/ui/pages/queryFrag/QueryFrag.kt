package com.crowd.client.ui.pages.startFrag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crowd.client.ui.pages.common.components.AppDateField
import com.crowd.client.ui.pages.common.components.AppForm
import com.crowd.client.ui.pages.common.components.AppTextField
import com.crowd.client.ui.pages.common.components.AppTimeField
import com.crowd.client.ui.pages.mainPage.components.Background
import com.crowd.client.ui.theme.CrowdClientTheme
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryFrag(
    modifier: Modifier = Modifier,
    onGetEstimation: (String, Long?, Pair<Int, Int>?) -> Unit = { _, _, _ -> }
) {
    val now = Calendar.getInstance()
    var location by remember { mutableStateOf("PES Canteen") }
    var dateState: DatePickerState? by remember {
        mutableStateOf(
            DatePickerState(
                Locale.getDefault(),
                now.timeInMillis
            )
        )
    }
    var timeState: TimePickerState? by remember {
        mutableStateOf(
            TimePickerState(
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
            )
        )
    }

    AppForm(
        modifier, "Enter Location",
        "Get Estimation", onSubmit = {
            onGetEstimation(
                location, dateState?.selectedDateMillis,
                timeState?.let { Pair(it.hour, it.minute) }
            )
        }
    ) {
        Column(
            Modifier.padding(5.dp, 0.dp), Arrangement.spacedBy(20.dp),
            Alignment.CenterHorizontally
        ) {
            AppTextField(
                label = "Place", value = location,
                modifier = Modifier.fillMaxWidth(),
                onClear = { location = "" },
                onValueChanged = { location = it }
            )
            AppDateField(
                label = "Date", value = dateState,
                modifier = Modifier.fillMaxWidth(),
                onClear = { dateState = null },
                onValueChanged = { dateState = it }
            )
            AppTimeField(
                label = "Time", value = timeState,
                modifier = Modifier.fillMaxWidth(),
                onClear = { timeState = null },
                onValueChanged = { timeState = it }
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
            QueryFrag(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter)
            ) { location, date, time ->
//                val f = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ", Locale.getDefault())
//                println(f.format(dateTime.time))
            }
        }
    }
}