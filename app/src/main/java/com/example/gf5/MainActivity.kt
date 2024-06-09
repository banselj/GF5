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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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
            val currentUser = auth.currentUser
            if (currentUser != null) {
                navigateBasedOnUserRole(currentUser.uid)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun navigateBasedOnUserRole(uid: String) {
        db.collection("users").document(uid).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val role = document.getString("role")
                if (role == "driver") {
                    val intent = Intent(this, DriverHomeActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, RiderHomeActivity::class.java)
                    startActivity(intent)
                }
                finish()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
