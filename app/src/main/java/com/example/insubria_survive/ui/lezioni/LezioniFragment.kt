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
import com.example.insubria_survive.databinding.FragmentLezioniBinding
import com.example.insubria_survive.utils.ConfirmAddEventDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.calendar.CalendarScopes
import com.example.insubria_survive.calendario.CalendarManager
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Lezione
import java.util.*

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

        // Configura RecyclerView e la callback per il click sul bottone Calendario
        binding.recyclerLezioni.layoutManager = LinearLayoutManager(requireContext())
        adapter = LezioniAdapter { lesson ->
            handleCalendarioClick(lesson)
        }
        binding.recyclerLezioni.adapter = adapter

        // Osserva le modifiche della lista raggruppata
        viewModel.lessonsListItems.observe(viewLifecycleOwner) { listItems ->
            adapter.submitList(listItems)
        }

        // Recupera le lezioni da Firebase e salva nel DB locale
        fetchLessonsFromFirebase()

        // Gestione del cambio di data nel CalendarView: estrae settimana e anno dalla data selezionata
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance(Locale.getDefault()).apply { set(year, month, dayOfMonth) }
            val selectedWeek = cal.get(Calendar.WEEK_OF_YEAR)
            val selectedYear = cal.get(Calendar.YEAR)
            Log.d(TAG, "Data selezionata: ${year}-${month + 1}-${dayOfMonth}, Settimana: $selectedWeek, Anno: $selectedYear")
            viewModel.loadLezioni(weekFilter = selectedWeek, yearFilter = selectedYear)
        }

        // Di default, carica la vista della settimana corrente (con anno corrente)
        viewModel.loadLezioni()

        return binding.root
    }


    /**
     * Recupera le lezioni da Firebase e le salva nel DB locale.
     */
    private fun fetchLessonsFromFirebase() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("lezione")
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d(TAG, "Recuperate ${querySnapshot.size()} lezioni da Firebase.")
                val repository = LocalDbRepository(requireContext())
                for (doc in querySnapshot.documents) {
                    val lezione = doc.toObject(com.example.insubria_survive.data.model.Lezione::class.java)
                    lezione?.let { it.id = doc.id }
                    if (lezione != null) {
                        repository.insertOrUpdateLezione(lezione)
                    }
                }
                viewModel.loadLezioni()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Errore nel recupero delle lezioni da Firebase", exception)
            }
    }

    /**
     * Gestisce il click sul bottone Calendario dell’item.
     * Mostra un dialog di conferma; se l'utente sceglie "Si", viene invocato il metodo per
     * aggiungere l’evento nel calendario.
     */
    private fun handleCalendarioClick(lesson: Lezione) {
        val dialog = ConfirmAddEventDialogFragment()
        dialog.callback = { result ->
            if (result == "si") {
                addLessonToCalendar(lesson)
            } else {
                Log.d(TAG, "L'utente ha annullato l'aggiunta dell'evento al calendario.")
                Toast.makeText(
                    requireContext(),
                    "Operazione annullata!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialog.show(parentFragmentManager, "ConfirmAddEventDialogFragment")
    }

    /**
     * Metodo per aggiungere l’evento della Lezione nel calendario.
     * In questo esempio si verifica se esiste già un account Google e si crea il credential
     * per poi utilizzare il CalendarManager.
     */
    private fun addLessonToCalendar(lesson: com.example.insubria_survive.data.model.Lezione) {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            // Crea il credential per Google Calendar
            val credential = GoogleAccountCredential.usingOAuth2(
                requireContext(),
                listOf(CalendarScopes.CALENDAR)
            )
            credential.selectedAccount = account.account

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
