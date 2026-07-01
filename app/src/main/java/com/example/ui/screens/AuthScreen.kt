package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onSignInSuccess: () -> Unit,
    onMockSignIn: suspend () -> Result<Unit>,
    onGoogleSignIn: suspend (Context) -> Result<Unit>,
    onEmailSignIn: suspend (String, String) -> Result<Unit>,
    onEmailSignUp: suspend (String, String) -> Result<Unit>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ParkZone",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "BANGALORE CENTRAL",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            letterSpacing = 2.dp.value.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(48.dp))

        if (errorMessage != null) {
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color(0xFF0F172A).copy(alpha = 0.8f),
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xFF0F172A).copy(alpha = 0.8f),
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF1E293B),
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color(0xFF0F172A).copy(alpha = 0.8f),
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xFF0F172A).copy(alpha = 0.8f),
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF1E293B),
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        val result = if (isSignUp) {
                            onEmailSignUp(email, password)
                        } else {
                            onEmailSignIn(email, password)
                        }
                        if (result.isSuccess) {
                            onSignInSuccess()
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Authentication failed."
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = email.isNotBlank() && password.isNotBlank()
            ) {
                Text(if (isSignUp) "Sign Up" else "Sign In", fontWeight = FontWeight.Bold)
            }
            
            TextButton(onClick = { isSignUp = !isSignUp }) {
                Text(if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up", color = MaterialTheme.colorScheme.primary)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("OR", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        val result = onGoogleSignIn(context)
                        if (result.isSuccess) {
                            onSignInSuccess()
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Sign-in failed."
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Sign in with Google", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        val result = onMockSignIn()
                        if (result.isSuccess) {
                            onSignInSuccess()
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
            ) {
                Text("Preview (Mock Sign-In)")
            }
        }
    }
}
