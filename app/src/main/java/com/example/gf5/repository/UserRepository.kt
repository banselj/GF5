package com.example.gf5.repository

import com.example.gf5.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val auth: FirebaseAuth) {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getUserById(userId: String): User {
        val documentSnapshot = firestore.collection("users").document(userId).get().await()
        if (documentSnapshot.exists()) {
            return documentSnapshot.toObject(User::class.java)
                ?: throw Exception("User data is corrupted.")
        } else {
            throw Exception("User not found.")
        }
    }

    suspend fun registerUser(email: String, password: String): FirebaseUser? {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        return authResult.user
    }

    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        return authResult.user
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun logoutUser() {
        auth.signOut()
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun updateUserEmail(newEmail: String): Boolean {
        val user = auth.currentUser
        return if (user != null) {
            user.updateEmail(newEmail).await()
            true
        } else {
            false
        }
    }

    suspend fun updateUserPassword(newPassword: String): Boolean {
        val user = auth.currentUser
        return if (user != null) {
            user.updatePassword(newPassword).await()
            true
        } else {
            false
        }
    }

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