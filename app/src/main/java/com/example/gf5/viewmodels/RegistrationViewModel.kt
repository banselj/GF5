
package com.example.gf5.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onfido.android.sdk.capture.config.Applicant
import com.onfido.android.sdk.capture.config.CaptureActivityOptions
import com.onfido.android.sdk.capture.config.DocumentType
import com.onfido.android.sdk.capture.config.FlowStep
import com.onfido.android.sdk.capture.config.Locale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    val onfidoRepository: OnfidoRepository // Assume you have a repository for Onfido interactions
) : ViewModel() {

    // Sealed class to represent registration states
    sealed class RegistrationState {
        object Loading : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    // LiveData for registration state
    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState

    // Sealed class to represent KYC states
    sealed class KYCState {
        object Loading : KYCState()
        data class Initiate(val config: CaptureActivityOptions) : KYCState()
        object Success : KYCState()
        data class Error(val message: String) : KYCState()
        data class Exited(val reason: String) : KYCState()
    }

    // LiveData for KYC state
    private val _kycState = MutableLiveData<KYCState>()
    val kycState: LiveData<KYCState> = _kycState

    /**
     * Registers the user with email and password.
     */
    fun registerUser(email: String, password: String) {
        _registrationState.value = RegistrationState.Loading
        viewModelScope.launch {
            try {
                // Implement your registration logic here, e.g., Firebase Auth
                // Example:
                // auth.createUserWithEmailAndPassword(email, password).await()
                // If successful:
                _registrationState.value = RegistrationState.Success
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.localizedMessage ?: "Registration failed")
            }
        }
    }

    /**
     * Initiates the KYC process by fetching the SDK token and configuring Onfido.
     */
    fun startKYC(sdkToken: String) {
        _kycState.value = KYCState.Loading
        viewModelScope.launch {
            try {
                // Configure Onfido
                val applicant = Applicant.builder()
                    .withFirstName("John")
                    .withLastName("Doe")
                    .withEmail("john.doe@example.com")
                    .build()

                val captureActivityOptions = CaptureActivityOptions.builder()
                    .withApplicant(applicant)
                    .withSDKToken(sdkToken)
                    .withWelcomeScreen(false)
                    .withFlowSteps(
                        FlowStep.WelcomeStep(true),
                        FlowStep.DocumentStep(DocumentType.PASSPORT)
                    )
                    .withLocale(Locale.ENGLISH)
                    .build()

                _kycState.value = KYCState.Initiate(captureActivityOptions)
            } catch (e: Exception) {
                _kycState.value = KYCState.Error(e.localizedMessage ?: "Failed to initiate KYC")
            }
        }
    }
}
