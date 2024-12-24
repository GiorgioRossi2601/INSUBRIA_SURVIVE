package com.example.insubria_survive.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val id: String?,
    val username: String?,
    val nome: String?,
    val cognome: String?
)