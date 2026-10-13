package com.aiv2ray.client.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiv2ray.client.ui.home.HomeScreen
import com.aiv2ray.client.ui.permissions.ConsentScreen
import com.aiv2ray.client.ui.theme.AiV2RayTheme
import com.aiv2ray.client.ui.theme.Steel
import com.aiv2ray.client.vm.ConsentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiV2RayTheme {
                RootScaffold()
            }
        }
    }
}

@Composable
private fun RootScaffold() {
    val consentVm: ConsentViewModel = viewModel()
    val accepted by consentVm.accepted.collectAsState(initial = false)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(0f to Steel.Base, 1f to Steel.Shadow))
    ) {
        if (accepted) {
            HomeScreen()
        } else {
            ConsentScreen(onAccept = { consentVm.accept() })
        }
    }
}
