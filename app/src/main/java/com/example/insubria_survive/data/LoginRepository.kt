package com.example.insubria_survive.data

import android.util.Log
import com.example.insubria_survive.data.model.LoggedInUser

/**
 * Oggetto singleton che gestisce l'autenticazione e la cache in memoria dello stato di login.
 */
object LoginRepository {

    private const val TAG = "LoginRepository"

    // Data source per l'autenticazione remoto
    private val dataSource: LoginDataSource = LoginDataSource()

    // Cache in memoria dell'utente autenticato
    var user: LoggedInUser? = null
        private set

    /**
     * Indica se un utente è attualmente loggato.
     */
    val isLoggedIn: Boolean
        get() = user != null

    init {
        // Se si prevede di memorizzare le credenziali in locale, è consigliabile criptarle.
        user = null
        Log.d(TAG, "Inizializzazione: nessun utente loggato.")
    }

    /**
     * Esegue il logout cancellando la cache e delegando l'azione al data source.
     */
    fun logout() {
        Log.d(TAG, "Esecuzione logout: rimozione utente dalla cache.")
        user = null
        dataSource.logout()
    }

    /**
     * Avvia il processo di login utilizzando il data source.
     *
     * @param username Il nome utente.
     * @param password La password.
     * @param onAfterLogin Callback per restituire il risultato del login.
     */
    fun login(username: String, password: String, onAfterLogin: (Result<LoggedInUser>) -> Unit) {
        Log.d(TAG, "Richiesta di login per utente: $username")
        dataSource.login(username, password) { result ->
            onInternalLogin(result, onAfterLogin)
        }
    }

    /**
     * Gestisce il risultato del login eseguito dal data source.
     *
     * @param result Il risultato ottenuto.
     * @param onAfterLogin Callback per propagare il risultato finale.
     */
    private fun onInternalLogin(result: Result<LoggedInUser>, onAfterLogin: (Result<LoggedInUser>) -> Unit) {
        Log.d(TAG, "Risultato del login ricevuto: $result")
        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }
        onAfterLogin(result)
    }

    /**
     * Salva l'utente autenticato nella cache in memoria.
     *
     * @param loggedInUser L'istanza dell'utente loggato.
     */
    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        Log.d(TAG, "Utente loggato salvato: $loggedInUser")
    }
}
