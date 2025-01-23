package com.example.insubria_survive.data.model

import com.google.firebase.Timestamp

/**
 * Data class che rappresenta una Lezione.
 *
 * @property id Identificativo univoco della lezione. Può essere nullo.
 * @property corso Nome del corso associato alla lezione. Può essere nullo.
 * @property data_inizio Data di inzio della lezione rappresentata tramite un oggetto Timestamp (Firebase). Può essere nullo.
 * @property data_fine Data di fine della lezione rappresentata tramite un oggetto Timestamp (Firebase). Può essere nullo.
 * @property aula Indica l'aula in cui si svolge la lezione. Può essere nullo.
 * @property padiglione Indica il padiglione in cui si trova l'aula. Può essere nullo.
 */
data class Lezione(
    var id: String?,
    val corso: String?,
    var data_inizio: Timestamp?,
    var data_fine: Timestamp?,
    val aula: String?,
    val padiglione: String?
) {
    constructor() : this("", "", null, null, "", "")
}