package com.example.gf5.repositories


import com.example.gf5.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val auth: FirebaseAuth) {

    // Initialize Firestore instance
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Fetches a user by their unique ID.
     *
     * @param userId The unique identifier of the user.
     * @return The [User] object.
     * @throws Exception if the user is not found or a network error occurs.
     */
    suspend fun getUserById(userId: String): User {
        val documentSnapshot = firestore.collection("users").document(userId).get().await()
        if (documentSnapshot.exists()) {
            return documentSnapshot.toObject(User::class.java)
                ?: throw Exception("User data is corrupted.")
        } else {
            throw Exception("User not found.")
        }
    }


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
