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
    fun sanitizeEmail(email: String?): String? {
        if (email.isNullOrBlank()) return null
        
        val trimmed = email.trim()
        if (trimmed.length > 100) return null
        
        // Check for spaces (not allowed in emails)
        if (trimmed.contains(" ")) return null
        
        // Check for double dots (not allowed)
        if (trimmed.contains("..")) return null
        
        // Check for XSS/injection patterns
        if (trimmed.contains("<") || trimmed.contains(">") || 
            trimmed.contains("script") || trimmed.contains("'") ||
            trimmed.contains("\"") || trimmed.contains("javascript:") ||
            trimmed.contains("onload") || trimmed.contains("--") ||
            trimmed.contains("UNION") || trimmed.contains("DROP")) {
            return null
        }
        
        // Use a simple regex pattern instead of android.util.Patterns for test compatibility
        // Must have exactly one @ symbol, text before and after it, and a domain with extension
        val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        
        // Additional validation: must have exactly one @ symbol
        if (trimmed.count { it == '@' } != 1) return null
        
        // Must not start or end with @
        if (trimmed.startsWith("@") || trimmed.endsWith("@")) return null
        
        return if (emailPattern.matches(trimmed)) {
            trimmed.lowercase()
        } else null
    }
    
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
        
        // Allow letters, digits, spaces, hyphens, apostrophes, periods
        val validPattern = Regex("^[a-zA-Z0-9\\s\\-'.]{2,50}$")
        return if (validPattern.matches(trimmed)) {
            trimmed
        } else null
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6 // Firebase minimum
    }
    
    fun isStrongPassword(password: String?): Boolean {
        if (password.isNullOrBlank()) return false
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
    private val requestCounts = mutableMapOf<String, MutableMap<String, Int>>()
    
    companion object {
        private const val LOGIN_LIMIT = 10
        private const val SIGNUP_LIMIT = 3
    }
    
    fun isAllowed(deviceId: String, action: String): Boolean {
        val deviceMap = requestCounts.getOrPut(deviceId) { mutableMapOf() }
        val currentCount = deviceMap.getOrDefault(action, 0)
        
        val limit = when (action) {
            "login" -> LOGIN_LIMIT
            "signup" -> SIGNUP_LIMIT
            else -> 100 // Default high limit
        }
        
        return if (currentCount < limit) {
            deviceMap[action] = currentCount + 1
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
