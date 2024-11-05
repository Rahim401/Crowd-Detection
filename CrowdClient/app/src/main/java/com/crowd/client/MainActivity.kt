package com.crowd.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.crowd.client.ui.pages.mainPage.MainPage
import com.crowd.client.ui.theme.CrowdClientTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainVM::class.java]
        viewModel.initializeModel(this)

        enableEdgeToEdge()
        setContent {
            CrowdClientTheme {
                MainPage(
                    Modifier.fillMaxSize(), viewModel.onFragment,
                    viewModel.isWaitingForResult,
                    viewModel.isEstimationSuccessful,
                    viewModel.estimationResult,
                    onAction = { viewModel.handelAction(it, this) }
                )
            }
        }
    }
}