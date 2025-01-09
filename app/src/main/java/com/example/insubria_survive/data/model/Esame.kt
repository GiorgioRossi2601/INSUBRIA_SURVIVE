package com.example.insubria_survive.data.model

import com.google.firebase.Timestamp

data class Esame(
    var id: String?,
    val corso: String?,
    val data: Timestamp?,
    val aula: String?,
    val padiglione: String?
){
    constructor() : this("", "", null, "", "")
}