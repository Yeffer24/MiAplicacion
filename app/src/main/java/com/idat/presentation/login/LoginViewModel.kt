package com.idat.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** LOGIN CON EMAIL Y CONTRASEÑA **/
    fun iniciarSesion(email: String, password: String) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _loginExitoso.value = true
                    } else {
                        _errorMessage.value = task.exception?.message ?: "Error desconocido"
                    }
                }
        }
    }

    /** LOGIN CON GOOGLE (Firebase Credential) **/
    fun loginWithGoogleCredential(result: SignInResult) {
        viewModelScope.launch {
            result.credential?.let { credential ->

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _loginExitoso.value = true
                        } else {
                            _errorMessage.value = task.exception?.message
                                ?: "Error al autenticar con Firebase"
                        }
                    }

            } ?: run {
                _errorMessage.value = result.errorMessage
                    ?: "Error al obtener credenciales de Google"
            }
        }
    }
}
