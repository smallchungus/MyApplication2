package com.example.myapplication.auth

/**
 * Simple rate limiter for security testing.
 */
class RateLimiter {
    
    private val requestCounts = mutableMapOf<String, MutableMap<String, Int>>()
    
    companion object {
        private const val LOGIN_LIMIT = 10
        private const val SIGNUP_LIMIT = 3
    }
    
    /**
     * Checks if a request is allowed based on rate limits.
     */
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
