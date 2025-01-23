package com.example.insubria_survive.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

/**
 * Data class che rappresenta un Padiglione.
 *
 * @property id Identificativo univoco del padiglione. Può essere nullo.
 * @property codice_padiglione Codice del padiglione. Può essere nullo.
 * @property descrizione Descrizione del padiglione. Può essere nullo.
 * @property ora_apertura Ora di apertura del padiglione. Può essere nullo.
 * @property ora_chiusura Ora di chiusura del padiglione. Può essere nullo.
 * @property posizione Posizione geografica del padiglione. Può essere nullo.
 */
data class Padiglione(
    var id: String?,
    val codice_padiglione: String?,
    val descrizione: String?,
    val ora_apertura: String?,
    val ora_chiusura: String?,
    val posizione: GeoPoint?
) {
    constructor() : this(null, null, null, null, null, GeoPoint(0.0, 0.0))
}