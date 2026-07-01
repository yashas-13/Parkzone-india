package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class AuthManager {
    val auth: FirebaseAuth?
        get() = try {
            Firebase.auth
        } catch (e: Exception) {
            null
        }

    val currentUser
        get() = auth?.currentUser

    suspend fun signInWithGoogle(context: Context, webClientId: String = "696437279947-ms5s66m6mpnuphailv95q4qrcp2nrugg.apps.googleusercontent.com"): Result<Unit> {
        if (webClientId.isEmpty() || webClientId == "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com") {
            return Result.failure(Exception("Google Web Client ID is missing. Please add it to Secrets."))
        }
        try {
            val credentialManager = CredentialManager.create(context)
            
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setNonce(hashedNonce)
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth?.signInWithCredential(firebaseCredential)?.await()
                return Result.success(Unit)
            } else if (credential is GoogleIdTokenCredential) {
                val idToken = credential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth?.signInWithCredential(firebaseCredential)?.await()
                return Result.success(Unit)
            } else {
                return Result.failure(Exception("Unexpected credential type: ${credential.type}"))
            }
        } catch (e: androidx.credentials.exceptions.NoCredentialException) {
            Log.e("AuthManager", "No Google account found", e)
            return Result.failure(Exception("No Google account found. Please sign in via Android Settings > Passwords & accounts."))
        } catch (e: Exception) {
            Log.e("AuthManager", "Google Sign In failed", e)
            val msg = if (e.message?.contains("No credentials available", ignoreCase = true) == true) {
                "No Google account found. Please sign in via Android Settings > Passwords & accounts."
            } else {
                e.message ?: "Google Sign In failed"
            }
            return Result.failure(Exception(msg, e))
        }
    }

    suspend fun mockSignIn(): Result<Unit> {
        // Fallback for preview environments without configured Firebase/Google
        return try {
            auth?.signInAnonymously()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        return try {
            auth?.signInWithEmailAndPassword(email, password)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthManager", "Email Sign In failed", e)
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        return try {
            auth?.createUserWithEmailAndPassword(email, password)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthManager", "Email Sign Up failed", e)
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.e("AuthManager", "Sign out failed", e)
        }
    }
}
