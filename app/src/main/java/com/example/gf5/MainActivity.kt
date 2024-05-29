package com.example.gf5

import android.content.Intent
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            GF5Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Welcome to GratisFare!",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }

        navigateBasedOnFirstLaunch()
    }

    private fun navigateBasedOnFirstLaunch() {
        val sharedPreferences = getSharedPreferences("GF5Preferences", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}
