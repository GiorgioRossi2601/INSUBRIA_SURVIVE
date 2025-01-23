package com.example.insubria_survive.ui.home

/** Data class che rappresenta una sezione della Home.
 *
 * @property immagine ResId dell'immagine da mostrare nella sezione.
 * @property testo ResId del testo da mostrare nella sezione.
 * @property destinatione ResId della destinazione da navigare quando viene cliccata la sezione.
 *
 * */
data class SezioneHome(
    val immagine: Int,
    val testo: Int,
    val destinatione: Int? = null
)
