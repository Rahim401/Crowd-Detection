package com.crowd.client.ui.pages.startFrag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crowd.client.ui.pages.common.components.AppForm
import com.crowd.client.ui.pages.common.components.AppTextField
import com.crowd.client.ui.pages.mainPage.components.Background
import com.crowd.client.ui.theme.CrowdClientTheme

@Composable
fun UserFrag(
    modifier: Modifier = Modifier,
    onSaveDetails: (String, String) -> Unit = { _, _ -> }
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    AppForm(
        modifier, "Enter your Details",
        "Save My Details", onSubmit = {
            onSaveDetails(name, email)
        }
    ) {
        Column(
            Modifier.padding(5.dp, 0.dp), Arrangement.spacedBy(20.dp),
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
                keyboardType = KeyboardType.Email,
                onClear = { email = "" },
                onValueChanged = { email = it }
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
            UserFrag(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter)
            )
        }
    }
}