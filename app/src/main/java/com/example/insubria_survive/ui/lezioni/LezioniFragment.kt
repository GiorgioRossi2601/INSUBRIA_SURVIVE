// LezioniFragment.kt
package com.example.insubria_survive.ui.lezioni

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Lezione
import com.example.insubria_survive.databinding.FragmentLezioniBinding
import com.google.firebase.firestore.FirebaseFirestore

class LezioniFragment : Fragment() {

    private var _binding: FragmentLezioniBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LezioniViewModel
    private lateinit var adapter: LezioniAdapter

    companion object {
        private const val TAG = "LezioniFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLezioniBinding.inflate(inflater, container, false)

        // Usa la factory per ottenere l'istanza del ViewModel
        val factory = LezioniViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(LezioniViewModel::class.java)

        // Configurazione della RecyclerView
        binding.recyclerLezioni.layoutManager = LinearLayoutManager(requireContext())
        adapter = LezioniAdapter()
        binding.recyclerLezioni.adapter = adapter

        // Osserva la lista raggruppata nel ViewModel
        viewModel.lessonsListItems.observe(viewLifecycleOwner) { listItems ->
            adapter.submitList(listItems)
        }

        // Recupera le lezioni da Firebase e le salva nel DB locale
        fetchLessonsFromFirebase()

        // Configurazione del CalendarView per filtrare per giorno (opzionale)
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            Log.d(TAG, "Data selezionata: $selectedDate")
            // Qui potresti implementare un metodo per filtrare le lezioni in base alla data selezionata.
        }

        return binding.root
    }

    /**
     * Recupera le lezioni da Firebase e le salva nel DB locale.
     * Al termine, il ViewModel caricherà automaticamente i dati (grazie all'inizializzazione).
     */
    private fun fetchLessonsFromFirebase() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("lezione")
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d(TAG, "Recuperate ${querySnapshot.size()} lezioni da Firebase.")
                val repository = LocalDbRepository(requireContext())
                for (doc in querySnapshot.documents) {
                    // Converte il documento in oggetto Lezione.
                    val lezione = doc.toObject(Lezione::class.java)
                    lezione?.let { it.id = doc.id }
                    if (lezione != null) {
                        repository.insertOrUpdateLezione(lezione)
                    }
                }
                // Ricarica i dati: in questo caso il ViewModel ha già l'inizializzazione nel costruttore,
                viewModel.loadLezioni() //per forzare il caricamento.
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Errore nel recuperare le lezioni da Firebase", exception)
                // In caso di errore, il ViewModel caricherà comunque i dati locali.
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
