package com.example.insubria_survive.data.model

/**
 * Rappresenta un record “Esame” + lo “stato” associato per l’utente.
 * "data" qui è una stringa (perché memorizzata in SQLite),
 * e "stato" è l'enum Stato (di default DA_FARE se non presente in preferenze_esame).
 */
data class EsameConStato(
    val id_esame: String,
    val corso: String,
    val data: String,
    val aula: String,
    val padiglione: String,
    val stato: Stato
)
