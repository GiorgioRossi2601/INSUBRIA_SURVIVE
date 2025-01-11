package com.example.insubria_survive.ui.esami

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.insubria_survive.data.model.Stato

class SharedEsameViewModel : ViewModel() {
    // LiveData per memorizzare lo stato aggiornato dell'esame
    private val _statoEsame = MutableLiveData<Stato>()
    val statoEsame: LiveData<Stato> get() = _statoEsame

    fun setStatoEsame(nuovoStato: Stato) {
        _statoEsame.value = nuovoStato
    }
}