package com.example.myapplication.auth

import java.util.regex.Pattern

/**
 * Input validation utilities for security.
 */
class InputValidator {
    
    companion object {
        private val EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )
        private val DISPLAY_NAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9\\s\\-'.]{2,50}$"
        )
    }
    
    /**
     * Sanitizes and validates email address.
     * Returns null if invalid.
     */
    fun sanitizeEmail(email: String?): String? {
        if (email.isNullOrBlank()) return null
        
        val trimmed = email.trim()
        if (trimmed.length > 100) return null
        
        // Check for basic XSS/injection patterns
        if (trimmed.contains("<") || trimmed.contains(">") || 
            trimmed.contains("script") || trimmed.contains("'") ||
            trimmed.contains("\"") || trimmed.contains("javascript:") ||
            trimmed.contains("onload") || trimmed.contains("--") ||
            trimmed.contains("UNION") || trimmed.contains("DROP")) {
            return null
        }
        
        return if (EMAIL_PATTERN.matcher(trimmed).matches()) {
            trimmed.lowercase()
        } else {
            null
        }
    }
    
    /**
     * Validates password strength.
     */
    fun isStrongPassword(password: String?): Boolean {
        if (password.isNullOrBlank()) return false
        if (password.length < 8) return false
        
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        
        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar
    }
    
    /**
     * Sanitizes display name.
     * Returns null if invalid.
     */
    fun sanitizeDisplayName(name: String?): String? {
        if (name.isNullOrBlank()) return null
        
        val trimmed = name.trim()
        if (trimmed.length < 2 || trimmed.length > 50) return null
        if (trimmed.isBlank()) return null
        
        // Check for XSS/injection patterns
        if (trimmed.contains("<") || trimmed.contains(">") || 
            trimmed.contains("script") || trimmed.contains("@") ||
            trimmed.contains("#") || trimmed.contains("$") ||
            trimmed.contains("%")) {
            return null
        }
        
        return if (DISPLAY_NAME_PATTERN.matcher(trimmed).matches()) {
            trimmed
        } else {
            null
        }
    }
}
