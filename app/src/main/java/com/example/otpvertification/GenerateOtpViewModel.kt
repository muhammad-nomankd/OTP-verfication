package com.example.otpvertification

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class GenerateOtpViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun generateOtp(activity: Activity, countryCode: String, phoneNumber: String) {
        val fullPhoneNumber = countryCode + phoneNumber

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(fullPhoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Pass activity for callback binding
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("OTP Verification", "Verification failed", e)
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    Log.e("OTP Verification", "Invalid request")
                }
                is FirebaseTooManyRequestsException -> {
                    Log.e("OTP Verification", "SMS quota exceeded")
                }
                is FirebaseAuthMissingActivityForRecaptchaException -> {
                    Log.e("OTP Verification", "Missing activity for reCAPTCHA")
                }
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // Store verificationId and token for further use
            Log.d("OTP Verification", "OTP code sent: Verification ID = $verificationId")
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Log.d("OTP Verification", "User signed in: $user")
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.e("OTP Verification", "Invalid verification code")
                    }
                }
            }
    }
}