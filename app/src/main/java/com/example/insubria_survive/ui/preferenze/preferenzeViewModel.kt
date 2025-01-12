package com.example.insubria_survive.ui.preferenze

import android.util.Log
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

    companion object {
        private const val TAG = "preferenzeViewModel"
    }

    init {
        loadPreferenze()
    }

    /**
     * Carica le preferenze per ciascun esame in base al loro stato.
     *
     * Per ogni esame presente nel database:
     * - Se esiste già una preferenza per l'utente, l'esame viene inserito nella lista corrispondente
     *   (DA_FARE, IN_FORSE, NON_FARE) in base al valore di preferenza salvato.
     * - Se non esiste, viene creata una preferenza di default con stato IN_FORSE e l'esame viene
     *   inserito in quella lista.
     *
     * In questo modo ogni esame compare in una sola lista, e il numero totale di preferenze sarà
     * uguale al numero di esami.
     */
    fun loadPreferenze() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Caricamento delle preferenze per l'utente: $username")

            // Recupera tutti gli esami presenti nel database locale
            val tuttiEsami: List<Esame> = repository.getAllEsami()
            Log.d(TAG, "Totale esami nel DB: ${tuttiEsami.size}")

            // Inizializzo liste mutabili per ciascun stato
            val listaDaFare = mutableListOf<Preferenza>()
            val listaInForse = mutableListOf<Preferenza>()
            val listaNonFare = mutableListOf<Preferenza>()

            // Per ogni esame, verifico se esiste già una preferenza per l'utente.
            // Se non esiste, creo una preferenza di default con stato "IN_FORSE"
            for (esame in tuttiEsami) {
                val prefExistente = repository.getPreferenzaByEsameAndUser(esame.id, username)
                if (prefExistente != null) {
                    when (prefExistente.stato) {
                        "DA_FARE" -> listaDaFare.add(prefExistente)
                        "NON_FARE" -> listaNonFare.add(prefExistente)
                        else -> listaInForse.add(prefExistente) // consideriamo anche eventuali altri casi come IN_FORSE
                    }
                } else {
                    // Non esiste una preferenza per questo esame: creazione di default IN_FORSE
                    val nuovaPreferenza = Preferenza(
                        id = null,              // L'id verrà assegnato dal DB
                        esame_codice = esame.id,
                        utente_codice = username,
                        stato = "IN_FORSE"
                    )
                    // Inserisco nel DB la nuova preferenza
                    repository.insertOrUpdatePreferenza(nuovaPreferenza)
                    listaInForse.add(nuovaPreferenza)
                }
            }

            // Log delle quantità per verificare che ogni esame compaia in una sola lista
            Log.d(TAG, "Preferenze DA_FARE finali: ${listaDaFare.size}")
            Log.d(TAG, "Preferenze IN_FORSE finali: ${listaInForse.size}")
            Log.d(TAG, "Preferenze NON_FARE finali: ${listaNonFare.size}")

            withContext(Dispatchers.Main) {
                _daFareList.value = listaDaFare
                _inForseList.value = listaInForse
                _nonFareList.value = listaNonFare
            }
        }
    }
}