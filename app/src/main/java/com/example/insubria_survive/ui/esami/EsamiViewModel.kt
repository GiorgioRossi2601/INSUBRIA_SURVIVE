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
 * Utilizza Firestore come sorgente dati remoto e salva i dati anche nel DB locale.
 */
class EsamiViewModel(
    private val localDbRepository: LocalDbRepository
) : ViewModel() {

    // Tag per il logging
    companion object {
        private const val TAG = "EsamiViewModel"
    }

    // Istanza di Firestore per il recupero degli esami
    private val db = Firebase.firestore

    // LiveData per la lista di esami
    private val _esamiList = MutableLiveData<List<Esame>>()
    val esamiList: LiveData<List<Esame>> get() = _esamiList

    // Listener per la snapshot di Firestore
    private var listenerRegistration: ListenerRegistration? = null

    init {
        Log.d(TAG, "Inizializzazione del ViewModel")
        // Avvio del recupero degli esami da Firestore
        fetchEsamiFromFirebase()
    }

    /**
     * Metodo di test per caricare una lista di esami in memoria.
     * Attualmente non utilizzato.
     */
    private fun loadTestEsami() {
        val testEsami = listOf(
            Esame("1", "Matematica", Timestamp.now(), "2", "Monte"),
            Esame("2", "Fisica", Timestamp.now(), "1", "Morselli")
        )
        _esamiList.value = testEsami

        // Salva in DB locale in background
        viewModelScope.launch(Dispatchers.IO) {
            testEsami.forEach { localDbRepository.insertOrUpdateEsame(it) }
        }
    }

    /**
     * Ascolta la collezione "esame" su Firestore.
     * Ad ogni aggiornamento, ordina i documenti, aggiorna la LiveData e salva i dati nel DB locale.
     */
    private fun fetchEsamiFromFirebase() {
        Log.d(TAG, "Inizio il recupero degli esami da Firestore")
        listenerRegistration = db.collection("esame")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "Errore nel recupero dei dati: ${exception.message}", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d(TAG, "Documenti trovati: ${snapshot.size()}")
                    val esami = snapshot.documents.mapNotNull { doc ->
                        try {
                            Log.d(TAG, "Documento recuperato: ${doc.data}")
                            // Converte il documento in un oggetto Esame e imposta l'id
                            val esame = doc.toObject(Esame::class.java)
                            esame?.let { it.id = doc.id }
                            esame
                        } catch (e: Exception) {
                            Log.e(TAG, "Errore nel parsing del documento: ${e.message}", e)
                            null
                        }
                    }.sortedWith(
                        compareBy(
                            { it.corso?.lowercase() },
                            { it.data?.toDate() }
                        )
                    )

                    if (esami.isEmpty()) {
                        Log.d(TAG, "Nessun documento valido trovato nella collezione")
                    }
                    // Aggiorna la LiveData per aggiornare l'UI
                    _esamiList.value = esami

                    // Salva gli esami nel DB locale in background
                    viewModelScope.launch(Dispatchers.IO) {
                        esami.forEach { esame ->
                            localDbRepository.insertOrUpdateEsame(esame)
                        }
                    }
                } else {
                    Log.d(TAG, "La snapshot è null")
                    _esamiList.value = emptyList()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        // Rimuove il listener per evitare memory leak
        listenerRegistration?.remove()
        Log.d(TAG, "onCleared: Listener rimosso")
    }
}
