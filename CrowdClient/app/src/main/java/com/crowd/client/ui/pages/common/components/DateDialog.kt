package com.crowd.client.ui.pages.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.crowd.client.ui.theme.CrowdClientTheme
import java.util.Calendar
import java.util.Locale

val ContainerWidth = 360.0.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
    dateState: DatePickerState = remember {
        val currentTime = Calendar.getInstance()
        DatePickerState(Locale.ROOT, currentTime.timeInMillis)
    },
    onCancel: () -> Unit = {},
    onPicked: (DatePickerState?) -> Unit = {},
) {
    val buttonPadding = PaddingValues(15.dp, 3.dp)
    BasicAlertDialog(
        onDismissRequest = onCancel,
        modifier = Modifier.wrapContentHeight(),
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val fadedSecondary = MaterialTheme.colorScheme.secondaryContainer.copy(0.3f)
        Surface(
            modifier = Modifier.requiredWidth(ContainerWidth),
            shape = DatePickerDefaults.shape
        ) {
            Column(Modifier.background(Color.White)) {
                DatePicker(
                    state = dateState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = fadedSecondary,
                        todayDateBorderColor = fadedSecondary,
                        titleContentColor = MaterialTheme.colorScheme.secondary,
                        headlineContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationContentColor = MaterialTheme.colorScheme.secondary,
                        todayContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        dateTextFieldColors = TextFieldDefaults.colors(
                            focusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            errorContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
//                        containerColor = Color.White
    //                clockDialColor = fadedSecondary,
    //                selectorColor = MaterialTheme.colorScheme.onSecondaryContainer,
    //                periodSelectorSelectedContainerColor = fadedSecondary,
    ////                periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    //                timeSelectorSelectedContainerColor = fadedSecondary,
    //                timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp),
                )
                AppButton(
                    "Confirm",
                    Modifier
                        .align(Alignment.End)
                        .fillMaxWidth(0.6f)
                        .padding(end = 10.dp, bottom = 10.dp),
                    buttonPadding, onClick = { onPicked(dateState) }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview() {
    CrowdClientTheme {
        AppDatePickerDialog(

        )
    }
}