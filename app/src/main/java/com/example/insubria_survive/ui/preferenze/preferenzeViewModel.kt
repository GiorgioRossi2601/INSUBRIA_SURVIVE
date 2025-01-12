package com.example.insubria_survive.ui.preferenze

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.EsameConPreferenza
import com.example.insubria_survive.data.model.Preferenza
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class preferenzeViewModel(
    private val repository: LocalDbRepository,
    private val username: String
) : ViewModel() {

    private val _daFareList = MutableLiveData<List<EsameConPreferenza>>()
    val daFareList: LiveData<List<EsameConPreferenza>> get() = _daFareList

    private val _inForseList = MutableLiveData<List<EsameConPreferenza>>()
    val inForseList: LiveData<List<EsameConPreferenza>> get() = _inForseList

    private val _nonFareList = MutableLiveData<List<EsameConPreferenza>>()
    val nonFareList: LiveData<List<EsameConPreferenza>> get() = _nonFareList

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
    /**
     * Per ogni esame presente nel database, cerca la relativa preferenza per l'utente.
     * Se non esiste, si considera lo stato di default "IN_FORSE" e, opzionalmente, si
     * può salvare la nuova preferenza.
     * Quindi crea un oggetto ExamWithPreference per ciascun esame e li suddivide in liste
     * in base al valore dello stato.
     */
    fun loadPreferenze() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Caricamento degli esami per l'utente: $username")
            val tuttiEsami: List<Esame> = repository.getAllEsami()
            Log.d(TAG, "Totale esami nel DB: ${tuttiEsami.size}")

            // Creo una lista composite: per ogni esame, uso lo stato presente (se esiste) altrimenti "IN_FORSE".
            val compositeList = tuttiEsami.map { esame ->
                val pref = repository.getPreferenzaByEsameAndUser(esame.id, username)
                val stato = pref?.stato ?: "IN_FORSE"
                // Se la preferenza non esiste, posso salvarla nel DB (opzionale)
                if (pref == null) {
                    val nuovaPref = com.example.insubria_survive.data.model.Preferenza(
                        id = null,
                        esame_codice = esame.id,
                        utente_codice = username,
                        stato = stato
                    )
                    repository.insertOrUpdatePreferenza(nuovaPref)
                }
                EsameConPreferenza(esame, stato)
            }

            // Suddivido in base allo stato
            val daFare = compositeList.filter { it.stato == "DA_FARE" }
            val inForse = compositeList.filter { it.stato == "IN_FORSE" }
            val nonFare = compositeList.filter { it.stato == "NON_FARE" }

            Log.d(TAG, "ExamWithPreference DA_FARE: ${daFare.size}")
            Log.d(TAG, "ExamWithPreference IN_FORSE: ${inForse.size}")
            Log.d(TAG, "ExamWithPreference NON_FARE: ${nonFare.size}")

            withContext(Dispatchers.Main) {
                _daFareList.value = daFare
                _inForseList.value = inForse
                _nonFareList.value = nonFare
            }
        }
    }
}