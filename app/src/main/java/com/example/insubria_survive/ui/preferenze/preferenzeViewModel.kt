package com.example.insubria_survive.ui.preferenze

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Preferenza
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class preferenzeViewModel(
    private val repository: LocalDbRepository,
    private val username: String
) : ViewModel() {

    private val _daFareList = MutableLiveData<List<Preferenza>>()
    val daFareList: LiveData<List<Preferenza>> get() = _daFareList

    private val _inForseList = MutableLiveData<List<Preferenza>>()
    val inForseList: LiveData<List<Preferenza>> get() = _inForseList

    private val _nonFareList = MutableLiveData<List<Preferenza>>()
    val nonFareList: LiveData<List<Preferenza>> get() = _nonFareList

    init {
        loadPreferenze()
    }


    fun loadPreferenze() {
        viewModelScope.launch(Dispatchers.IO) {
            // Carica gli esami per ciascun stato in base all'utente
            val daFare = repository.getPreferenzeByStato("DA_FARE", username)
            var inForse = repository.getPreferenzeByStato("IN_FORSE", username)
            val nonFare = repository.getPreferenzeByStato("NON_FARE", username)

            if (inForse.isEmpty()) {
                // Se non ci sono preferenze, carica tutti gli esami e crea preferenze di default
                val tuttiEsami: List<Esame> = repository.getAllEsami()
                inForse = tuttiEsami.map { esame ->
                    Preferenza(
                        id = null,              // L'id verrà generato dal DB (AUTOINCREMENT)
                        esame_codice = esame.id,
                        utente_codice = username,
                        stato = "IN_FORSE"
                    )
                }
                // Salva queste preferenze nel DB (opzionale)
                inForse.forEach { pref ->
                    repository.insertOrUpdatePreferenza(pref)
                }
            }

            withContext(Dispatchers.Main) {
                _daFareList.value = daFare
                _inForseList.value = inForse
                _nonFareList.value = nonFare
            }
        }
    }
}