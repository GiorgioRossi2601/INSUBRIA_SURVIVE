package com.example.insubria_survive.data.model

import com.google.firebase.Timestamp
import java.io.Serializable

/**
 * Data class che rappresenta un Esame.
 *
 * @property id Identificativo univoco dell'esame. Può essere nullo.
 * @property corso Nome del corso associato all'esame. Può essere nullo.
 * @property data Data e ora dell'esame rappresentate tramite un oggetto Timestamp (Firebase). Può essere nullo.
 * @property aula Indica l'aula in cui si svolge l'esame. Può essere nullo.
 * @property padiglione Indica il padiglione in cui si trova l'aula, se presente. Può essere nullo.
 */
data class Esame(
    var id: String?,
    val corso: String?,
    var data: Timestamp?,
    val aula: String?,
    val padiglione: String?,
) : Serializable {
    constructor() : this("", "", null, "", "")
}
