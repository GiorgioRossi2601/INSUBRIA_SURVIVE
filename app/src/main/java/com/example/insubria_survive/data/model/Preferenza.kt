package com.example.insubria_survive.data.model

/**
 * Data class che rappresenta una Preferenza associata ad un esame e ad un utente.
 *
 * @property id Identificativo univoco della preferenza. Può essere nullo.
 * @property esame_codice Codice identificativo dell'esame a cui si riferisce la preferenza. Può essere nullo.
 * @property utente_codice Identificativo (o username) dell'utente a cui appartiene la preferenza.
 * @property stato Stato della preferenza.
 */
data class Preferenza(
    val id: String?,
    var esame_codice: String?,
    val utente_codice: String?,
    val stato: String?
) {
    /**
     * Costruttore di default richiesto, che inizializza le proprietà con valori di default.
     */
    constructor() : this("", null, "", "")
}
