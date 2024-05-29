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
}
