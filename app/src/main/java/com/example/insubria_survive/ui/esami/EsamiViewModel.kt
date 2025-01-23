package com.example.insubria_survive.ui.esami

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Esame
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel per gestire i dati relativi agli esami.
 *
 * Utilizza Firestore come sorgente dati remoto e salva i dati nel DB locale.
 *
 * @param localDbRepository Repository per l'accesso al DB locale.
 */
class EsamiViewModel(
    private val localDbRepository: LocalDbRepository
) : ViewModel() {

    companion object {
        private const val TAG = "EsamiViewModel"
    }

    private val db = Firebase.firestore

    private val _esamiList = MutableLiveData<List<Esame>>()
    val esamiList: LiveData<List<Esame>> get() = _esamiList

    private var listenerRegistration: ListenerRegistration? = null

    init {
        Log.d(TAG, "init: Inizializzazione del ViewModel")
        fetchEsamiFromFirebase()
    }

    /**
     * Ascolta la collezione "esame" su Firestore.
     * Ad ogni aggiornamento, ordina i documenti, aggiorna la LiveData e salva i dati nel DB locale.
     */
    private fun fetchEsamiFromFirebase() {
        Log.d(TAG, "fetchEsamiFromFirebase: Inizio il recupero degli esami da Firestore")
        listenerRegistration = db.collection("esame")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(
                        TAG,
                        "fetchEsamiFromFirebase: Errore nel recupero dei dati: ${exception.message}",
                        exception
                    )
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d(TAG, "fetchEsamiFromFirebase: Documenti trovati: ${snapshot.size()}")
                    val esami = snapshot.documents.mapNotNull { doc ->
                        try {
                            Log.d(TAG, "fetchEsamiFromFirebase: Documento recuperato: ${doc.data}")
                            val esame = doc.toObject(Esame::class.java)
                            esame?.apply { id = doc.id }
                        } catch (e: Exception) {
                            Log.e(
                                TAG,
                                "fetchEsamiFromFirebase: Errore nel parsing del documento: ${e.message}",
                                e
                            )
                            null
                        }
                    }.sortedWith(
                        compareBy(
                            { it.corso?.lowercase() },
                            { it.data?.toDate() }
                        )
                    )

                    if (esami.isEmpty()) {
                        Log.d(
                            TAG,
                            "fetchEsamiFromFirebase: Nessun documento valido trovato nella collezione"
                        )
                    }

                    _esamiList.value = esami

                    // Salva gli esami nel DB locale in background
                    viewModelScope.launch(Dispatchers.IO) {
                        esami.forEach { localDbRepository.insertOrUpdateEsame(it) }
                    }
                } else {
                    Log.d(TAG, "fetchEsamiFromFirebase: Snapshot null")
                    _esamiList.value = emptyList()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
        Log.d(TAG, "onCleared: Listener rimosso")
    }
}
