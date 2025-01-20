package com.example.insubria_survive.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Padiglione
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimelineViewModel(
    private val localDbRepository: LocalDbRepository
) : ViewModel() {
    companion object {
        private const val TAG = "TimelineViewModel"
    }

    private val db = Firebase.firestore

    private val _padiglioniList = MutableLiveData<List<Padiglione>>()
    val padiglioniList: LiveData<List<Padiglione>> get() = _padiglioniList

    private var listenerRegistration: ListenerRegistration? = null

    init {
        Log.d(TAG, "init: Inizializzazione del ViewModel")
        fetchPadiglioniFromFirebase()
    }

    /**
     * Ascolta la collezione "esame" su Firestore.
     * Ad ogni aggiornamento, ordina i documenti, aggiorna la LiveData e salva i dati nel DB locale.
     */
    private fun fetchPadiglioniFromFirebase() {
        Log.d(TAG, "fetchPadiglioniFromFirebase: Inizio il recupero dei padiglioni da Firestore")
        listenerRegistration = db.collection("padiglione")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(
                        TAG,
                        "fetchPadiglioniFromFirebase: Errore nel recupero dei dati: ${exception.message}",
                        exception
                    )
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d(TAG, "fetchPadiglioniFromFirebase: Documenti trovati: ${snapshot.size()}")
                    val padiglioni = snapshot.documents.mapNotNull { doc ->
                        try {
                            Log.d(
                                TAG,
                                "fetchPadiglioniFromFirebase: Documento recuperato: ${doc.data}"
                            )
                            val padiglione = doc.toObject(Padiglione::class.java)
                            padiglione?.apply { id = doc.id }
                        } catch (e: Exception) {
                            Log.e(
                                TAG,
                                "fetchPadiglioniFromFirebase: Errore nel parsing del documento: ${e.message}",
                                e
                            )
                            null
                        }
                    }.sortedWith(
                        compareBy(
                            { it.codice_padiglione},
                            { it.ora_apertura}
                        )
                    )

                    if (padiglioni.isEmpty()) {
                        Log.d(
                            TAG,
                            "fetchEsamiFromFirebase: Nessun documento valido trovato nella collezione"
                        )
                    }

                    _padiglioniList.value = padiglioni

                    // Salva gli esami nel DB locale in background
                    viewModelScope.launch(Dispatchers.IO) {
                        padiglioni.forEach { localDbRepository.insertOrUpdatePadiglione(it) }
                    }
                } else {
                    Log.d(TAG, "fetchEsamiFromFirebase: Snapshot null")
                    _padiglioniList.value = emptyList()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
        Log.d(TAG, "onCleared: Listener rimosso")
    }
}



