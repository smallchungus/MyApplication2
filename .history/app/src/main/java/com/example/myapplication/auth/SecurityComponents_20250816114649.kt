package com.example.myapplication.auth

/**
 * Standalone security components for testing and reuse.
 * 
 * These classes provide the same security functionality as the nested classes
 * in SecureAuthRepository but can be instantiated independently for testing.
 * 
 * @author ParentCare Security Team
 * @since 1.0.0
 */

/**
 * Input validator for security and data integrity.
 */
class InputValidator {
    fun sanitizeEmail(email: String): String? {
        val trimmed = email.trim().lowercase()
        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            trimmed
        } else null
    }
    
    fun sanitizeDisplayName(name: String): String? {
        val trimmed = name.trim()
        return if (trimmed.length in 2..50 && trimmed.all { it.isLetterOrDigit() || it.isWhitespace() }) {
            trimmed
        } else null
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6 // Firebase minimum
    }
    
    fun isStrongPassword(password: String): Boolean {
        return password.length >= 8 && 
               password.any { it.isUpperCase() } && 
               password.any { it.isLowerCase() } && 
               password.any { it.isDigit() } && 
               password.any { !it.isLetterOrDigit() }
    }
}

/**
 * Rate limiter to prevent abuse and control costs.
 */
class RateLimiter {
    private val attempts = mutableMapOf<String, MutableList<Long>>()
    
    fun isAllowed(identifier: String, operation: String): Boolean {
        val key = "$identifier:$operation"
        val now = System.currentTimeMillis()
        val window = 3600000L // 1 hour
        val operationAttempts = attempts.getOrPut(key) { mutableListOf() }
        
        // Remove old attempts outside window
        operationAttempts.removeAll { it < now - window }
        
        // Check limits
        val limit = when (operation) {
            "login" -> 10
            "signup" -> 3
            else -> 5
        }
        
        return if (operationAttempts.size < limit) {
            operationAttempts.add(now)
            true
        } else {
            false
        }
    }
}

/**
 * Simple LRU cache for reducing Firestore read costs.
 * 
 * @param maxSize Maximum number of items in cache
 */
class LRUCache<K, V>(private val maxSize: Int) {
    private val cache = LinkedHashMap<K, V>(maxSize, 0.75f, true)
    
    @Synchronized
    fun get(key: K): V? = cache[key]
    
    @Synchronized
    fun put(key: K, value: V) {
        if (cache.size >= maxSize) {
            val eldest = cache.entries.iterator().next()
            cache.remove(eldest.key)
        }
        cache[key] = value
    }
    
    @Synchronized
    fun clear() = cache.clear()
    
    @Synchronized
    fun size() = cache.size
}
