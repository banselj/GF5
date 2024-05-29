package com.example.gf5

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.safetynet.SafetyNet
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.common.api.ApiException


class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private var email: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val registerButton: Button = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            email = emailEditText.text.toString().trim()
            password = passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                showReCAPTCHA()
            }
        }
    }

    private fun registerUser() {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    navigateToRideRequestActivity()
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToRideRequestActivity() {
        val intent = Intent(this, RideRequestActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        if (password.isEmpty() || !password.matches(passwordRegex.toRegex())) {
            Toast.makeText(this, "Password must be at least 8 characters and include a number, an upper, a lower, and a special character", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun showReCAPTCHA() {
        SafetyNet.getClient(this).verifyWithRecaptcha("YOUR_SITE_KEY")
            .addOnSuccessListener(this) { response ->
                if (response.tokenResult?.isNotEmpty() == true) {
                    registerUser()
                }
            }
            .addOnFailureListener(this) { e ->
                if (e is ApiException) {
                    Toast.makeText(this, "Error verifying reCAPTCHA: ${e.statusCode}", Toast.LENGTH_LONG).show()
                }
            }
    }


}
