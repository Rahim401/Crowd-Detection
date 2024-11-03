package com.crowd.client.ui.pages.common.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crowd.client.ui.theme.CrowdClientTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePickerDialog(
    timeState: TimePickerState = remember {
        val currentTime = Calendar.getInstance()
        TimePickerState(
            initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentTime.get(Calendar.MINUTE),
            false
        )
    },
    onCancel: () -> Unit = {},
    onPicked: (TimePickerState?) -> Unit = {},
) {
    val buttonPadding = PaddingValues(15.dp, 3.dp)
    DatePickerDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            AppButton(
                "Confirm", Modifier
                    .fillMaxWidth(0.6f)
                    .padding(end = 10.dp, bottom = 10.dp),
                buttonPadding, onClick = { onPicked(timeState) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val fadedSecondary = MaterialTheme.colorScheme.secondaryContainer.copy(0.3f)
        TimePicker(
            state = timeState,
            colors = TimePickerDefaults.colors(
                clockDialColor = fadedSecondary,
                selectorColor = MaterialTheme.colorScheme.onSecondaryContainer,
                periodSelectorSelectedContainerColor = fadedSecondary,
//                periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                timeSelectorSelectedContainerColor = fadedSecondary,
                timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp),
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview() {
    CrowdClientTheme {
        AppTimePickerDialog()
    }
}