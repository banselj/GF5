package com.example.gf5

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.onfido.android.sdk.capture.Onfido
import com.onfido.android.sdk.capture.OnfidoConfig
import com.onfido.android.sdk.capture.OnfidoFactory
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.ui.options.FlowStep
import com.onfido.android.sdk.capture.upload.Captures

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var verifyIDButton: Button
    private var email: String = ""
    private var password: String = ""
    private val onfido: Onfido by lazy { OnfidoFactory.create(this).client }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        verifyIDButton = findViewById(R.id.verifyIDButton)

        registerButton.setOnClickListener {
            email = emailEditText.text.toString().trim()
            password = passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                registerUser()
            }
        }

        verifyIDButton.setOnClickListener {
            startKYCProcess()
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

    private fun startKYCProcess() {
        val applicantId = "YOUR_APPLICANT_ID"  // Replace with the actual applicant ID
        try {
            val onfidoConfig = OnfidoConfig.builder(this)
                .withSDKToken("YOUR_ONFIDO_SDK_TOKEN")
                .withCustomFlow(arrayOf(
                    FlowStep.CAPTURE_DOCUMENT,
                    FlowStep.CAPTURE_FACE
                ))
                .build()

            onfido.startActivityForResult(this, ONFIDO_REQUEST_CODE, onfidoConfig)
        } catch (e: OnfidoException) {
            Toast.makeText(this, "Error starting Onfido: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ONFIDO_REQUEST_CODE) {
            onfido.handleActivityResult(resultCode, data, object : Onfido.OnfidoResultListener {
                override fun onError(exception: OnfidoException) {
                    Toast.makeText(this@RegistrationActivity, "KYC verification failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

                override fun userCompleted(captures: Captures) {
                    Toast.makeText(this@RegistrationActivity, "KYC verification successful", Toast.LENGTH_SHORT).show()
                }

                override fun userExited(exitCode: com.onfido.android.sdk.capture.ExitCode) {
                    Toast.makeText(this@RegistrationActivity, "KYC verification exited: $exitCode", Toast.LENGTH_SHORT).show()
                }
            })
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

    companion object {
        private const val ONFIDO_REQUEST_CODE = 1
    }
}
