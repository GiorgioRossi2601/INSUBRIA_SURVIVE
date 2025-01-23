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
 * Carica dal database locale gli esami e li suddivide in liste in base allo stato (DA_FARE, IN_FORSE, NON_FARE).
 * Se un esame non ha una preferenza, viene impostato lo stato di default "IN_FORSE" e salvato.
 */
class PreferenzeViewModel(
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
        private const val TAG = "PreferenzeViewModel"
    }

    init {
        Log.d(TAG, "Inizializzazione PreferenzeViewModel per utente: $username")
        loadPreferenze()
    }

    /**
     * Carica le preferenze per ciascun esame.
     *
     * Per ogni esame nel database:
     * - Se esiste una preferenza per l'utente, la usa.
     * - Altrimenti, imposta lo stato di default "IN_FORSE" e la salva.
     *
     * Viene creato un oggetto [EsameConPreferenza] per ogni esame e la lista viene suddivisa
     * in base allo stato: DA_FARE, IN_FORSE, NON_FARE.
     */
    fun loadPreferenze() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Caricamento degli esami per l'utente: $username")
            val tuttiEsami: List<Esame> = repository.getAllEsami()
            Log.d(TAG, "Totale esami nel DB: ${tuttiEsami.size}")

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

            val daFare = compositeList.filter { it.stato == "DA_FARE" }
            val inForse = compositeList.filter { it.stato == "IN_FORSE" }
            val nonFare = compositeList.filter { it.stato == "NON_FARE" }

            Log.d(TAG, "Esami DA_FARE: ${daFare.size}")
            Log.d(TAG, "Esami IN_FORSE: ${inForse.size}")
            Log.d(TAG, "Esami NON_FARE: ${nonFare.size}")

            withContext(Dispatchers.Main) {
                _daFareList.value = daFare
                _inForseList.value = inForse
                _nonFareList.value = nonFare
            }
        }
    }
}
