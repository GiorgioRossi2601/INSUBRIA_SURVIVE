package com.example.insubria_survive.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Padiglione(
    var id: String?,
    val codice_padiglione: String?,
    val descrizione: String?,
    val ora_apertura: String?,
    val ora_chiusura: String?,
    val posizione: GeoPoint?
){
    constructor() : this(null,null,null,null,null,GeoPoint(0.0, 0.0))
}