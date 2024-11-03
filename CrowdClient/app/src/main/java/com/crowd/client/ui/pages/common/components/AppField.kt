package com.crowd.client.ui.pages.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crowd.client.R
import com.crowd.client.ui.theme.CrowdClientTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppField(
    label: String, value: String,
    modifier: Modifier = Modifier,
    onClear: () -> Unit = {},
    onEdit: () -> Unit = {},
) {
    Column(modifier, Arrangement.spacedBy(1.dp)) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            Modifier
                .heightIn(24.dp)
                .fillMaxWidth(),
            Arrangement.SpaceBetween, Alignment.CenterVertically
        ) {
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(0.9f)
                    .clickable(
                        onClick = onEdit,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
            )

            AnimatedVisibility(
                value.isNotEmpty(),
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier.padding(start = 5.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cancel),
                    contentDescription = "Clear Field",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiary),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp)
                        .clickable(
                            onClick = onClear,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
//                    .clickable(interactionSource = null, onClick = onClear)
                )
            }
        }
        Column(modifier = Modifier.fillMaxWidth().height(1.dp)) {
            AnimatedVisibility(
                value.isBlank(), Modifier,
                enter = fadeIn(), exit = fadeOut(),
            ) { HorizontalDivider(Modifier) }
        }
    }
}

@Composable
fun AppTextField(
    label: String, value: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onClear: () -> Unit = {}, onValueChanged: (String) -> Unit = {}
) {
    Column(modifier, Arrangement.spacedBy(1.dp)) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            Modifier
                .heightIn(24.dp)
                .fillMaxWidth(),
            Arrangement.SpaceBetween, Alignment.CenterVertically
        ) {
            BasicTextField(
                value, onValueChanged,
                textStyle = MaterialTheme.typography.titleLarge
                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType
                )
            )

            AnimatedVisibility(
                value.isNotEmpty(),
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier.padding(start = 5.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cancel),
                    contentDescription = "Clear Field",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiary),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp)
                        .clickable(
                            onClick = onClear,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
//                    .clickable(interactionSource = null, onClick = onClear)
                )
            }
        }
        Column(modifier = Modifier.fillMaxWidth().height(1.dp)) {
            AnimatedVisibility(
                value.isBlank(), Modifier,
                enter = fadeIn(), exit = fadeOut(),
            ) { HorizontalDivider(Modifier) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimeField(
    label: String, modifier: Modifier = Modifier,
    value: TimePickerState? = null,
    onClear: () -> Unit = {},
    onValueChanged: (TimePickerState?) -> Unit = {},
) {
    var showPicker by remember { mutableStateOf(false) }
    if(showPicker) AppTimePickerDialog(
        onCancel = { showPicker = false },
        onPicked = {
            onValueChanged(it)
            showPicker = false
        }
    )

    val textTime = value?.let {
        "${it.hour % 12}:${it.minute} " +
                if(it.hour < 12) "AM" else "PM"
    } ?: ""
    AppField(label, textTime, modifier, onClear) { showPicker = true }
}


var dateFormat: SimpleDateFormat = SimpleDateFormat(
    "EEE, d MMM yyyy", Locale.getDefault()
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppDateField(
    label: String, modifier: Modifier = Modifier,
    value: DatePickerState? = null,
    onClear: () -> Unit = {},
    onValueChanged: (DatePickerState?) -> Unit = {},
) {
    var showPicker by remember { mutableStateOf(false) }
    if(showPicker) AppDatePickerDialog(
        onCancel = { showPicker = false },
        onPicked = {
            onValueChanged(it)
            showPicker = false
        }
    )

    val textDate = value?.selectedDateMillis?.let {
        dateFormat.format(Date(it))
    } ?: ""
    AppField(label, textDate, modifier, onClear) { showPicker = true }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview() {
    CrowdClientTheme {
        Column(
            Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(30.dp),
            Arrangement.spacedBy(10.dp), Alignment.CenterHorizontally
        ) {
            Box {
                var value by remember { mutableStateOf("Mugesh Ravichandran") }
                AppField(
                    label = "Name", value = value,
                    modifier = Modifier.fillMaxWidth(),
                    onEdit = { value = "Hi" },
                    onClear = { value = "" }
                )
            }
            Box {
                var value by remember { mutableStateOf("gta@mail.com") }
                AppTextField(
                    label = "Email", value = value,
                    modifier = Modifier.fillMaxWidth(),
                    onClear = { value = "" },
                    onValueChanged = { value = it }
                )
            }
            Box {
                var value by remember { mutableStateOf("gta@mail.com") }
                AppTextField(
                    label = "Email", value = value,
                    modifier = Modifier.fillMaxWidth(),
                    onClear = { value = "" },
                    onValueChanged = { value = it }
                )
            }
            Box {
                var value: TimePickerState?  by remember { mutableStateOf(null) }
                AppTimeField(
                    label = "Time", value = value,
                    modifier = Modifier.fillMaxWidth(),
                    onClear = { value = null },
                    onValueChanged = { value = it }
                )
            }
            Box {
                var value: DatePickerState?  by remember { mutableStateOf(null) }
                AppDateField(
                    label = "Date", value = value,
                    modifier = Modifier.fillMaxWidth(),
                    onClear = { value = null },
                    onValueChanged = { value = it }
                )
            }
        }
    }
}