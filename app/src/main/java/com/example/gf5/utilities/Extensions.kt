package com.example.gf5.utilities

import android.util.Patterns
import android.view.View
import android.widget.EditText

// Extension function to validate email
fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

// Extension function to validate password strength
fun String.isValidPassword(): Boolean {
    // Minimum 8 characters, at least one uppercase, one lowercase, one number, and one special character
    val passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
    return this.matches(passwordRegex.toRegex())
}

// Extension functions for View visibility
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

// Extension function to trim and validate if EditText is not empty
fun EditText.isNotEmpty(): Boolean {
    return this.text.toString().trim().isNotEmpty()
}

// Extension function to get trimmed text from EditText
fun EditText.trimmedText(): String {
    return this.text.toString().trim()
}
