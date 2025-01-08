package com.example.insubria_survive.ui.esami

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.insubria_survive.data.model.Esame
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.Timestamp
import java.io.IOException

class EsamiViewModel() : ViewModel() {

    val db = Firebase.firestore
    private val _esamiList = MutableLiveData<List<Esame>>()
    val esamiList: LiveData<List<Esame>> get() = _esamiList

    private var listenerRegistration: ListenerRegistration? = null

    init {
        //loadTestEsami()
        fetchEsamiFromFirebase()
    }

    private fun loadTestEsami() {
        val testEsami = listOf(
            Esame("1", "Matematica", Timestamp.now(), "2", "Monte"),
            Esame("2", "Fisica", Timestamp.now(), "1", "Morselli")
        )
        _esamiList.value = testEsami
    }


    private fun fetchEsamiFromFirebase() {
        listenerRegistration = db.collection("esame")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    println("Errore durante il recupero dei dati: ${exception.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    println("Numero di documenti trovati: ${snapshot.size()}")
                    val esami = snapshot.documents.mapNotNull { doc ->
                        try {
                            println("Documento recuperato: ${doc.data}")
                            doc.toObject(Esame::class.java)
                        } catch (e: Exception) {
                            println("Errore nel parsing del documento: ${e.message}")
                            null
                        }
                    }.sortedWith(compareBy({ it.corso?.lowercase() }, { it.data?.toDate() })) //ordina per nome e data di esame
                    if (esami.isEmpty()) {
                        println("Nessun documento valido trovato nella collezione.")
                    }
                    _esamiList.value = esami
                } else {
                    println("La snapshot è null.")
                    _esamiList.value = emptyList()
                }
            }
    }


    override fun onCleared() {
        super.onCleared()
        // Rimuovi il listener per evitare memory leak
        listenerRegistration?.remove()
    }

}
