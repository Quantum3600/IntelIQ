package com.techbrains.inteliq.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.presentation.theme.Ink
import com.techbrains.inteliq.presentation.theme.Paper
import com.techbrains.inteliq.presentation.theme.PaperRaised
import com.techbrains.inteliq.presentation.theme.Signal
import com.techbrains.inteliq.presentation.theme.SignalDim

@Composable
fun AuthScreen(
    state: AuthUiState,
    onIntent: (AuthIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Paper),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = PaperRaised,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Header (Left-aligned per DESIGN.md §2.3)
                Text(
                    text = "IntelIQ",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Signal
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (state.isRegistering) "Create your student account" else "Sign in to continue your learning",
                    fontSize = 15.sp,
                    color = Ink.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Error Banner
                AnimatedVisibility(visible = state.errorMessage != null) {
                    state.errorMessage?.let { msg ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = msg,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Name field (Registration only)
                AnimatedVisibility(visible = state.isRegistering) {
                    Column {
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { onIntent(AuthIntent.NameChanged(it)) },
                            label = { Text("Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Signal,
                                unfocusedBorderColor = SignalDim
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Email field
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onIntent(AuthIntent.EmailChanged(it)) },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Signal,
                        unfocusedBorderColor = SignalDim
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onIntent(AuthIntent.PasswordChanged(it)) },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Signal,
                        unfocusedBorderColor = SignalDim
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = { onIntent(AuthIntent.SubmitAuth) },
                    enabled = !state.isLoading && state.email.isNotBlank() && state.password.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Signal,
                        contentColor = PaperRaised
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = PaperRaised,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .height(20.dp)
                                .width(20.dp)
                        )
                    } else {
                        Text(
                            text = if (state.isRegistering) "Register" else "Sign In",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Login / Register
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.isRegistering) "Already have an account?" else "Don't have an account?",
                        fontSize = 13.sp,
                        color = Ink.copy(alpha = 0.7f)
                    )
                    TextButton(onClick = { onIntent(AuthIntent.ToggleAuthMode) }) {
                        Text(
                            text = if (state.isRegistering) "Sign In" else "Register",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Signal
                        )
                    }
                }
            }
        }
    }
}
