package com.example.insubria_survive.data.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Esame(
    var id: String?,
    val corso: String?,
    var data: Timestamp?,
    val aula: String?,
    val padiglione: String?,
) {
    constructor() : this("", "", null, "", "")

}