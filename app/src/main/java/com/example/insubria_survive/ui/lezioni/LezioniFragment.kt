package com.example.insubria_survive.ui.lezioni

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insubria_survive.calendario.CalendarManager
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Lezione
import com.example.insubria_survive.databinding.FragmentLezioniBinding
import com.example.insubria_survive.utils.ConfirmAddEventDialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.calendar.CalendarScopes
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

/**
 * Fragment per la visualizzazione delle lezioni.
 *
 * Mostra un elenco (raggruppato per settimana) e consente di:
 * - Visualizzare le lezioni filtrate per data tramite un CalendarView
 * - Salvare una lezione nel calendario (tramite dialog di conferma)
 */
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

        // Inizializza il ViewModel tramite Factory
        val factory = LezioniViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(LezioniViewModel::class.java)

        setupRecyclerView()
        observeLessons()
        fetchLessonsFromFirebase()

        // Gestione del cambio di data nel CalendarView: filtra per settimana e anno
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance(Locale.getDefault()).apply { set(year, month, dayOfMonth) }
            val selectedWeek = cal.get(Calendar.WEEK_OF_YEAR)
            val selectedYear = cal.get(Calendar.YEAR)
            Log.d(TAG, "Data selezionata: $year-${month + 1}-$dayOfMonth, Settimana: $selectedWeek, Anno: $selectedYear")
            viewModel.loadLezioni(weekFilter = selectedWeek, yearFilter = selectedYear)
        }

        // Carica le lezioni della settimana corrente
        viewModel.loadLezioni()

        return binding.root
    }

    /**
     * Configura il RecyclerView e l'adapter per la lista delle lezioni.
     */
    private fun setupRecyclerView() {
        binding.recyclerLezioni.layoutManager = LinearLayoutManager(requireContext())
        adapter = LezioniAdapter { lesson ->
            handleCalendarioClick(lesson)
        }
        binding.recyclerLezioni.adapter = adapter
    }

    /**
     * Osserva la LiveData della lista degli item e aggiorna l'adapter.
     */
    private fun observeLessons() {
        viewModel.lessonsListItems.observe(viewLifecycleOwner) { listItems ->
            adapter.submitList(listItems)
        }
    }

    /**
     * Recupera le lezioni da Firebase e le salva nel database locale.
     */
    private fun fetchLessonsFromFirebase() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("lezione")
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d(TAG, "Recuperate ${querySnapshot.size()} lezioni da Firebase.")
                val repository = LocalDbRepository(requireContext())
                querySnapshot.documents.forEach { doc ->
                    val lezione = doc.toObject(Lezione::class.java)?.apply { id = doc.id }
                    lezione?.let { repository.insertOrUpdateLezione(it) }
                }
                // Ricarica le lezioni (eventuale aggiornamento della vista)
                viewModel.loadLezioni()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Errore nel recupero delle lezioni da Firebase", exception)
            }
    }

    /**
     * Gestisce il click sul bottone Calendario dell’item lezione.
     *
     * Mostra un dialog di conferma e, se accettato, aggiunge l’evento al calendario.
     */
    private fun handleCalendarioClick(lesson: Lezione) {
        val dialog = ConfirmAddEventDialogFragment().apply {
            callback = { result ->
                if (result == "si") {
                    addLessonToCalendar(lesson)
                } else {
                    Log.d(TAG, "L'utente ha annullato l'aggiunta dell'evento al calendario.")
                    Toast.makeText(requireContext(), "Operazione annullata!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show(parentFragmentManager, "ConfirmAddEventDialogFragment")
    }

    /**
     * Aggiunge l’evento della lezione al calendario.
     *
     * Se l'utente è loggato con Google, crea il credential e utilizza [CalendarManager].
     */
    private fun addLessonToCalendar(lesson: Lezione) {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            val credential = GoogleAccountCredential.usingOAuth2(
                requireContext(), listOf(CalendarScopes.CALENDAR)
            ).apply {
                selectedAccount = account.account
            }
            val calendarManager = CalendarManager(requireContext(), credential)
            calendarManager.addLessonToCalendar(lesson) { success, info ->
                requireActivity().runOnUiThread {
                    if (success) {
                        Toast.makeText(
                            requireContext(),
                            "Evento Lezione creato con successo!\nVisualizzalo qui: $info",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Errore nella creazione dell'evento: $info",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Devi effettuare il login Google per salvare l'evento.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
