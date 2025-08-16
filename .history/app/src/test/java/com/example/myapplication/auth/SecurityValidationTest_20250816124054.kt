package com.example.myapplication.auth

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.runners.JUnit4

/**
 * Security validation tests to ensure app meets security requirements.
 */
@RunWith(JUnit4::class)
class SecurityValidationTest {
    
    private lateinit var inputValidator: InputValidator
    
    @Before
    fun setup() {
        inputValidator = InputValidator()
    }
    
    @Test
    fun rejectsInvalidEmailFormats() {
        val invalidEmails = listOf(
            "",
            "invalid",
            "@domain.com",
            "user@",
            "user space@domain.com",
            "user..double.dot@domain.com",
            "a".repeat(100) + "@domain.com"  // Too long
        )
        
        invalidEmails.forEach { email ->
            assertNull("Should reject invalid email: '$email'", 
                      inputValidator.sanitizeEmail(email))
        }
    }
    
    @Test
    fun acceptsValidEmailFormats() {
        val validEmails = listOf(
            "user@domain.com",
            "test.email+tag@example.org",
            "user123@sub.domain.co.uk",
            "firstname.lastname@company.com"
        )
        
        validEmails.forEach { email ->
            assertNotNull("Should accept valid email: '$email'", 
                         inputValidator.sanitizeEmail(email))
        }
    }
    
    @Test
    fun strongPasswordValidationWorks() {
        // Weak passwords that should be rejected
        val weakPasswords = listOf(
            "weak",           // Too short
            "12345678",       // Only numbers
            "password",       // Only lowercase
            "PASSWORD",       // Only uppercase
            "Pass123",        // Missing special character
            "pass@123"        // Missing uppercase
        )
        
        // Strong passwords that should be accepted
        val strongPasswords = listOf(
            "StrongPass1!",
            "MySecure@Pass2",
            "Complex$Password9",
            "Test@Password123"
        )
        
        weakPasswords.forEach { password ->
            assertFalse("Should reject weak password: '$password'", 
                       inputValidator.isStrongPassword(password))
        }
        
        strongPasswords.forEach { password ->
            assertTrue("Should accept strong password", 
                      inputValidator.isStrongPassword(password))
        }
    }
    
    @Test
    fun displayNameSanitizationWorks() {
        // Valid display names
        val validNames = listOf(
            "John Doe",
            "Jane Smith",
            "Robert Johnson III",
            "Maria Garcia-Lopez"
        )
        
        // Invalid display names
        val invalidNames = listOf(
            "",                           // Empty
            "A",                          // Too short
            "A".repeat(100),              // Too long
            "John<script>alert(1)</script>", // XSS attempt
            "User@#$%",                   // Invalid characters
            "   ",                        // Only whitespace
        )
        
        validNames.forEach { name ->
            assertNotNull("Should accept valid name: '$name'", 
                         inputValidator.sanitizeDisplayName(name))
        }
        
        invalidNames.forEach { name ->
            assertNull("Should reject invalid name: '$name'", 
                      inputValidator.sanitizeDisplayName(name))
        }
    }
    
    @Test
    fun rateLimiterPreventsAbuse() {
        val rateLimiter = RateLimiter()
        val deviceId = "test-device"
        
        // Test login rate limiting
        repeat(10) {
            assertTrue("Should allow requests within limit", 
                      rateLimiter.isAllowed(deviceId, "login"))
        }
        
        // 11th request should be blocked
        assertFalse("Should block requests exceeding limit", 
                   rateLimiter.isAllowed(deviceId, "login"))
        
        // Test signup rate limiting (lower limit)
        val newDeviceId = "new-device"
        repeat(3) {
            assertTrue("Should allow signup requests within limit", 
                      rateLimiter.isAllowed(newDeviceId, "signup"))
        }
        
        // 4th signup request should be blocked
        assertFalse("Should block signup requests exceeding limit", 
                   rateLimiter.isAllowed(newDeviceId, "signup"))
    }
    
    @Test
    fun inputSanitizationPreventsXSS() {
        val maliciousInputs = listOf(
            "<script>alert('xss')</script>",
            "javascript:alert('xss')",
            "onload=alert('xss')",
            "<img src=x onerror=alert('xss')>",
            "'; DROP TABLE users; --",
            "admin' OR '1'='1",
            "user@domain.com' UNION SELECT * FROM users --"
        )
        
        maliciousInputs.forEach { input ->
            val sanitized = inputValidator.sanitizeEmail(input)
            assertNull("Should reject malicious input: '$input'", sanitized)
        }
    }
    
    @Test
    fun passwordStrengthRequirementsAreEnforced() {
        // Test minimum length requirement
        assertFalse("Should reject passwords shorter than 8 characters", 
                   inputValidator.isStrongPassword("Short1"))
        
        // Test character type requirements
        assertFalse("Should reject passwords without uppercase", 
                   inputValidator.isStrongPassword("lowercase1!"))
        assertFalse("Should reject passwords without lowercase", 
                   inputValidator.isStrongPassword("UPPERCASE1!"))
        assertFalse("Should reject passwords without numbers", 
                   inputValidator.isStrongPassword("NoNumbers!"))
        assertFalse("Should reject passwords without special characters", 
                   inputValidator.isStrongPassword("NoSpecial1"))
        
        // Test valid strong password
        assertTrue("Should accept strong password", 
                  inputValidator.isStrongPassword("StrongPass1!"))
    }
}
