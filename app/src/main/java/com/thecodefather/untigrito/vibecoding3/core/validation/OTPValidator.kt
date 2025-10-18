package com.example.vibecoding3.core.validation

/**
 * Validador de código OTP
 */
object OTPValidator {
    private const val OTP_LENGTH = 5
    
    fun isValid(otp: String): Boolean {
        return otp.length == OTP_LENGTH && otp.all { it.isDigit() }
    }
}
