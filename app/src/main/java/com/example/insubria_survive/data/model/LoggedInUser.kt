package com.example.insubria_survive.data.model

/**
 * Data class che cattura le informazioni dell'utente loggato ottenute da LoginRepository.
 *
 * @property id Identificativo univoco dell'utente.
 * @property username Il nome utente.
 * @property nome Il nome dell'utente.
 * @property cognome Il cognome dell'utente.
 */
data class LoggedInUser(
    val id: String?,
    val username: String?,
    val nome: String?,
    val cognome: String?
)
