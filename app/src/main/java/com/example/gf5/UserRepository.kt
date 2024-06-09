package com.example.gf5

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class UserRepository(private val auth: FirebaseAuth) {

    // Function to register a new user
    suspend fun registerUser(email: String, password: String): FirebaseUser? {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        return authResult.user
    }

    // Function to login user
    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        return authResult.user
    }

    // Function to check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Function to get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Function to log out user
    fun logoutUser() {
        auth.signOut()
    }

    // Function to reset password
    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    // Function to update user's email
    suspend fun updateUserEmail(newEmail: String): Boolean {
        val user = auth.currentUser
        return if (user != null) {
            user.updateEmail(newEmail).await()
            true
        } else {
            false
        }
    }

    // Function to update user's password
    suspend fun updateUserPassword(newPassword: String): Boolean {
        val user = auth.currentUser
        return if (user != null) {
            user.updatePassword(newPassword).await()
            true
        } else {
            false
        }
    }

    // Function to delete user account
    suspend fun deleteUserAccount(): Boolean {
        val user = auth.currentUser
        return if (user != null) {
            user.delete().await()
            true
        } else {
            false
        }
    }
}
