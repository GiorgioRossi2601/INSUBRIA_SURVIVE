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

/**
 * ViewModel per gestire le preferenze degli esami di un utente.
 *
 * Carica dal database (sia remoto che locale) gli esami e li suddivide in liste
 * in base allo stato (DA_FARE, IN_FORSE, NON_FARE).
 *
 * Se un esame non ha ancora una preferenza, viene impostato lo stato di default "IN_FORSE"
 * e (opzionalmente) salvato nel database.
 */
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

    // Tag per il logging
    companion object {
        private const val TAG = "preferenzeViewModel"
    }

    init {
        Log.d(TAG, "Inizializzazione preferenzeViewModel per utente: $username")
        loadPreferenze()
    }

    /**
     * Carica le preferenze per ciascun esame.
     *
     * Per ogni esame presente nel database viene:
     * - Verificata l'esistenza di una preferenza per l'utente.
     * - Se non esiste, viene considerato lo stato di default "IN_FORSE" e salvata.
     * - Viene creato un oggetto EsameConPreferenza.
     *
     * Le preferenze vengono poi suddivise in tre liste in base al loro stato.
     */
    fun loadPreferenze() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Caricamento degli esami per l'utente: $username")
            val tuttiEsami: List<Esame> = repository.getAllEsami()
            Log.d(TAG, "Totale esami nel DB: ${tuttiEsami.size}")

            // Crea la lista composita considerando la preferenza esistente o un default "IN_FORSE"
            val compositeList = tuttiEsami.map { esame ->
                val pref = repository.getPreferenzaByEsameAndUser(esame.id, username)
                val stato = pref?.stato ?: "IN_FORSE"
                if (pref == null) {
                    // Salvataggio opzionale della preferenza di default
                    val nuovaPref = Preferenza(
                        id = null,
                        esame_codice = esame.id,
                        utente_codice = username,
                        stato = stato
                    )
                    repository.insertOrUpdatePreferenza(nuovaPref)
                    Log.d(TAG, "Preferenza di default creata per esame ${esame.id}")
                }
                EsameConPreferenza(esame, stato)
            }

            // Suddivide la lista composita in base allo stato
            val daFare = compositeList.filter { it.stato == "DA_FARE" }
            val inForse = compositeList.filter { it.stato == "IN_FORSE" }
            val nonFare = compositeList.filter { it.stato == "NON_FARE" }

            Log.d(TAG, "Esami DA_FARE: ${daFare.size}")
            Log.d(TAG, "Esami IN_FORSE: ${inForse.size}")
            Log.d(TAG, "Esami NON_FARE: ${nonFare.size}")

            // Aggiorna le LiveData sul thread principale
            withContext(Dispatchers.Main) {
                _daFareList.value = daFare
                _inForseList.value = inForse
                _nonFareList.value = nonFare
            }
        }
    }
}
