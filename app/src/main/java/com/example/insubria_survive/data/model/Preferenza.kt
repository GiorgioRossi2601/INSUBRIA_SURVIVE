package com.example.insubria_survive.data.model


data class Preferenza(
    val id: String?,
    var esame_codice: String?,
    val utente_codice: String?,
    val stato: String?
){
    constructor() : this("", null, "", "")
}

