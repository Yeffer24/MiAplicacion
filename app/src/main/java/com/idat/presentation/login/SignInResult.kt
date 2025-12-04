package com.idat.presentation.login

import com.google.firebase.auth.AuthCredential

/**
 * Resultado del proceso de Google Sign-In
 */
data class SignInResult(
    val credential: AuthCredential? = null,
    val errorMessage: String? = null
)
