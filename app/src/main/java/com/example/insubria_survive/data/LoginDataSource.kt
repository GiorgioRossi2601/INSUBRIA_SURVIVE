package com.example.insubria_survive.data

import android.util.Log
import com.example.insubria_survive.data.model.LoggedInUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException

/**
 * Classe che gestisce l'autenticazione tramite credenziali di login
 * e il recupero delle informazioni utente da Firestore.
 */
class LoginDataSource {

    // TAG per i log
    companion object {
        private const val TAG = "LoginDataSource"
    }

    // Istanza del database Firestore per operazioni remote
    private val db = Firebase.firestore

    /**
     * Esegue il login cercando un utente nella collezione "users" con le credenziali fornite.
     *
     * @param username Il nome utente da autenticare.
     * @param password La password associata.
     * @param onLoginCompleted Callback per restituire il risultato dell'operazione di login.
     */
    fun login(
        username: String,
        password: String,
        onLoginCompleted: (Result<LoggedInUser>) -> Unit
    ) {
        Log.d(TAG, "Tentativo di login per utente: $username")
        db.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Chiamata a Firestore riuscita per utente: $username")
                if (!documents.isEmpty) {
                    // Se l'utente viene trovato, si estrae il primo documento
                    val user = documents.elementAt(0)
                    Log.d(TAG, "Utente trovato: ${user.getString("username")}")
                    onLoginCompleted(
                        Result.Success(
                            LoggedInUser(
                                id = user.getString("id"),
                                username = user.getString("username"),
                                nome = user.getString("nome"),
                                cognome = user.getString("cognome")
                            )
                        )
                    )
                } else {
                    Log.w(TAG, "Nessun utente trovato per le credenziali fornite.")
                    onLoginCompleted(Result.Error(IOException("Error logging in")))
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Errore durante il tentativo di login per utente: $username", e)
                onLoginCompleted(Result.Error(IOException("Error logging in", e)))
            }
    }

    /**
     * Esegue il logout dell'utente.
     * TODO: Implementare la revoca dell'autenticazione se necessario.
     */
    fun logout() {
        Log.d(TAG, "Esecuzione logout")
        // TODO: revoke authentication
    }
}
