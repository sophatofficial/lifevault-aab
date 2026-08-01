package com.example.utils

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Utility class to interact with Android BiometricPrompt API for security authentication.
 */
object BiometricHelper {

    /**
     * Checks if the device supports biometric or device credential authentication.
     */
    fun canAuthenticate(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        } else {
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        }
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> true
            else -> false
        }
    }

    /**
     * Prompts the user with [BiometricPrompt] for fingerprint, face, or device passcode authentication.
     *
     * @param activity Hosting [FragmentActivity] required by BiometricPrompt
     * @param title Title text on the biometric prompt
     * @param subtitle Subtitle description text
     * @param onSuccess Invoked when biometric / PIN authentication succeeds
     * @param onError Invoked with error message when authentication fails or is cancelled
     */
    fun promptBiometricAuth(
        activity: FragmentActivity,
        title: String = "LifeVault Private Access",
        subtitle: String = "Authenticate via Biometrics or Device PIN to view notes",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED || errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    onError("Authentication cancelled.")
                } else {
                    onError(errString.toString())
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Verification failed. Tap to try again.")
            }
        }

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptInfoBuilder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else {
            promptInfoBuilder.setNegativeButtonText("Cancel")
        }

        try {
            val biometricPrompt = BiometricPrompt(activity, executor, callback)
            biometricPrompt.authenticate(promptInfoBuilder.build())
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e.localizedMessage ?: "Biometric prompt error")
        }
    }
}
