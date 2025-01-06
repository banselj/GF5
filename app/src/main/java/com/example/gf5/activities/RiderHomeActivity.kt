package com.example.gf5.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gf5.ui.theme.GF5Theme
import com.example.gf5.viewModels.RiderHomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RiderHomeActivity : ComponentActivity() {

    // Obtain the ViewModel instance via Hilt
    private val riderHomeViewModel: RiderHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GF5Theme {
                RiderHomeScreen()
            }
        }

        // Initialize or observe ViewModel data if necessary
        // For example:
        // riderHomeViewModel.fetchRiderData()
    }

    @Composable
    private fun RiderHomeScreen() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RiderHomeContent()
        }
    }

    @Composable
    private fun RiderHomeContent() {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Rider Home",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
