package com.example.insubria_survive.data.model

import com.google.firebase.Timestamp

data class Lezione(
    var id: String?,
    val corso: String?,
    var data_inizio: Timestamp?,
    var data_fine: Timestamp?,
    val aula: String?,
    val padiglione: String?
){
    constructor() : this("", "", null, null, "", "")
}