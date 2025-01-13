package com.example.insubria_survive.data.model

/**
 * Data class che associa un Esame ad uno stato di preferenza.
 *
 * @property esame L'oggetto Esame associato.
 * @property stato Lo stato della preferenza per l'esame.
 */
data class EsameConPreferenza(
    val esame: Esame,
    val stato: String
)
