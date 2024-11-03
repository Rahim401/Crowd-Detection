package com.crowd.client.ui.pages.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crowd.client.ui.theme.CrowdClientTheme

@Composable
fun AppForm(
    modifier: Modifier = Modifier, title: String = "",
    buttonText: String = "", onSubmit: () -> Unit = {},
    fieldContent: @Composable () -> Unit = {},
) {
    Surface(
        modifier, ShapeDefaults.Medium,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(Modifier.padding(25.dp)) {
            if(title.isNotEmpty()) Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 5.dp, bottom = 20.dp)
            )
            fieldContent()
            if(buttonText.isNotEmpty()) AppButton(
                text = buttonText,
                modifier = Modifier.padding(top = 30.dp, bottom = 10.dp)
                    .fillMaxWidth().height(60.dp),
                onClick = onSubmit
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CrowdClientTheme {
        Column(Modifier, Arrangement.spacedBy(10.dp), Alignment.CenterHorizontally) {
            AppForm(Modifier.wrapContentSize(), "Enter your Details", "Save My Details") {
                var name by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                Column(
                    Modifier.padding(5.dp, 0.dp),
                    Arrangement.spacedBy(20.dp),
                    Alignment.CenterHorizontally
                ) {
                    AppTextField(
                        label = "Name", value = name,
                        modifier = Modifier.fillMaxWidth(),
                        onClear = { name = "" },
                        onValueChanged = { name = it }
                    )
                    AppTextField(
                        label = "Email", value = email,
                        modifier = Modifier.fillMaxWidth(),
                        onClear = { email = "" },
                        onValueChanged = { email = it }
                    )
                }
            }
        }
    }
}