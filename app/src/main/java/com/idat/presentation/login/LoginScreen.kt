package com.idat.presentation.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.idat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginExitoso by viewModel.loginExitoso.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Google Sign-In Client configurado
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("944113440911-e6gprivm1354fnlqoi6dnpiibh75glml.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Launcher para el flujo de Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                viewModel.loginWithGoogleCredential(SignInResult(credential = credential))
            } catch (e: ApiException) {
                viewModel.loginWithGoogleCredential(
                    SignInResult(errorMessage = "Error al iniciar sesión con Google: ${e.message}")
                )
            }
        }
    }

    // Navegar cuando se loguea
    if (loginExitoso) {
        LaunchedEffect(Unit) {
            navController.navigate("catalogo") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // Mostrar mensaje de error
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5EDE8),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(horizontal = 32.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                    // Logo H&M
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_hm),
                        contentDescription = "Logo H&M",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 32.dp)
                    )

                    // Campo Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Username or E-mail") },
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF222222),
                            unfocusedBorderColor = Color(0xFFCCCCCC),
                            focusedLabelColor = Color(0xFF222222),
                            unfocusedLabelColor = Color(0xFF666666),
                            cursorColor = Color(0xFF222222),
                            focusedTextColor = Color(0xFF222222),
                            unfocusedTextColor = Color(0xFF222222)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = Color(0xFF666666)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF222222),
                            unfocusedBorderColor = Color(0xFFCCCCCC),
                            focusedLabelColor = Color(0xFF222222),
                            unfocusedLabelColor = Color(0xFF666666),
                            cursorColor = Color(0xFF222222),
                            focusedTextColor = Color(0xFF222222),
                            unfocusedTextColor = Color(0xFF222222)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Remember me y Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = { },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFFE50010),
                                    uncheckedColor = Color(0xFF999999)
                                )
                            )
                            Text(
                                text = "Remember me",
                                fontSize = 14.sp,
                                color = Color(0xFF222222)
                            )
                        }
                        
                        TextButton(onClick = { }) {
                            Text(
                                text = "Forgot Password?",
                                fontSize = 14.sp,
                                color = Color(0xFFE50010)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón Login
                    Button(
                        onClick = { viewModel.iniciarSesion(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE50010),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Divider con "Or"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFCCCCCC),
                            thickness = 1.dp
                        )
                        Text(
                            text = "  Or  ",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFCCCCCC),
                            thickness = 1.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón Google
                    OutlinedButton(
                        onClick = {
                            googleSignInClient.signOut().addOnCompleteListener {
                                val signInIntent = googleSignInClient.signInIntent
                                googleSignInLauncher.launch(signInIntent)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF222222)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCCCCC))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
