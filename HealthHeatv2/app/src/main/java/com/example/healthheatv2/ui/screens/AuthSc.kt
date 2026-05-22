package com.example.healthheatv2.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthheatv2.ui.viewmodel.AuthState
import com.example.healthheatv2.ui.viewmodel.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import androidx.credentials.CustomCredential

// Theme Colors
private val SurfaceDark = Color(0xFF131313)
private val PrimaryGold = Color(0xFFFFD79B)
private val PrimaryContainer = Color(0xFFFFB300)
private val OnPrimaryFixed = Color(0xFF281900)

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
        }
    }
    AuthScreenContent(
        authState = authState,
        onGoogleSignInClick = {
            coroutineScope.launch {
                try {
                    // TODO: Replace with your Web Client ID from Firebase
                    val webClientId = "106908032906-h890o1rbd0k8d3bneqavhre92i45sfmd.apps.googleusercontent.com"

                    val credentialManager = CredentialManager.create(context)
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .setAutoSelectEnabled(true)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(context, request)
                    val credential = result.credential

                    // THE FIX: Open the CustomCredential envelope first!
                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        viewModel.signInWithGoogleToken(googleIdTokenCredential.idToken)
                    } else {
                        Log.e("Auth", "Unexpected credential type: ${credential.javaClass.name}")
                    }
                } catch (e: Exception) {
                    Log.e("Auth", "Google Sign In Failed", e)
                }
            }
        }
    )
}

@Composable
fun AuthScreenContent(
    authState: AuthState,
    onGoogleSignInClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Branding
            Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = null,
                tint = PrimaryGold,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "HEALTHEAT",
                color = PrimaryGold,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your intelligent nutrition assistant.",
                color = Color(0xFFD6C4AC),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Error Display
            if (authState is AuthState.Error) {
                Text(
                    text = authState.message,
                    color = Color(0xFFFFB4AB),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Google Sign-In Button
            Button(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainer,
                    contentColor = OnPrimaryFixed
                ),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = OnPrimaryFixed, modifier = Modifier.size(24.dp))
                } else {
                    Text("Continue with Google", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. THE PREVIEWS
// ---------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
fun PreviewAuthScreenIdle() {
    AuthScreenContent(
        authState = AuthState.Idle,
        onGoogleSignInClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
fun PreviewAuthScreenLoading() {
    AuthScreenContent(
        authState = AuthState.Loading,
        onGoogleSignInClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF131313)
@Composable
fun PreviewAuthScreenError() {
    AuthScreenContent(
        authState = AuthState.Error("Network connection failed. Please try again."),
        onGoogleSignInClick = {}
    )
}