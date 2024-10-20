// File: com/example/gf5/viewmodel/ProfileViewModel.kt
package com.example.gf5.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    // LiveData for user profile
    private val _userProfile = MutableLiveData<Map<String, Any?>>()
    val userProfile: LiveData<Map<String, Any?>> = _userProfile

    // LiveData for operation status
    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    /**
     * Loads the user profile from Firestore.
     */
    fun loadUserProfile(user: FirebaseUser) {
        viewModelScope.launch {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        _userProfile.value = document.data
                    } else {
                        _status.value = "User profile does not exist"
                    }
                }
                .addOnFailureListener { exception ->
                    _status.value = "Failed to load user profile: ${exception.message}"
                }
        }
    }

    /**
     * Updates the user profile in Firestore and FirebaseAuth.
     */
    fun updateUserProfile(user: FirebaseUser, name: String, email: String) {
        if (name.isEmpty()) {
            _status.value = "Name cannot be empty"
            return
        }

        if (email.isEmpty()) {
            _status.value = "Email cannot be empty"
            return
        }

        // Update Firestore
        val userUpdates = mapOf(
            "name" to name
        )
        db.collection("users").document(user.uid).update(userUpdates)
            .addOnSuccessListener {
                _status.value = "Profile updated successfully"
            }
            .addOnFailureListener { exception ->
                _status.value = "Failed to update profile: ${exception.message}"
            }

        // Update FirebaseAuth Email
        if (email != user.email) {
            user.updateEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _status.value = "Email updated successfully"
                    } else {
                        _status.value = "Failed to update email: ${task.exception?.message}"
                    }
                }
        }
    }

    /**
     * Logs out the current user.
     */
    fun logout() {
        auth.signOut()
    }
}
