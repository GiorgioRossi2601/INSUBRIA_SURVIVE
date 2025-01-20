package com.example.insubria_survive.ui.timeline

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Padiglione
import com.example.insubria_survive.databinding.FragmentTimelineBinding
import com.example.insubria_survive.ui.esami.EsamiFragment
import com.example.insubria_survive.utils.dialog.ConfirmAddEventDialogFragment
import com.example.insubria_survive.utils.dialog.ConfirmMapsPadiglioneDialogFragment

class TimelineFragment : Fragment() {

    companion object {
        private const val TAG = "TimelineFragment"
    }

    private var _binding: FragmentTimelineBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var timelineViewModel: TimelineViewModel
    private lateinit var adapter: TimelineAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Inizializzazione del fragment")
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)

        // Inizializzazione del repository locale e del ViewModel tramite factory
        val repository = LocalDbRepository(requireContext())
        val factory = TimelineViewModelFactory(repository)
        timelineViewModel = ViewModelProvider(this, factory).get(TimelineViewModel::class.java)

        setupTimelineRecyclerView()

        observePadiglioni()

        return binding.root
    }

    private fun setupTimelineRecyclerView(){
        adapter = TimelineAdapter(emptyList())
        binding.recyclerViewTimeline.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerViewTimeline.adapter = adapter

        adapter.setOnItemClickListener(object : TimelineAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val padiglioneSelezionato = adapter.getPadiglioneAt(position)
                showConfermaMapsPadiglione(padiglioneSelezionato)
            }
        })
    }

    private fun observePadiglioni() {
        timelineViewModel.padiglioniList.observe(viewLifecycleOwner) { padiglioni ->
            padiglioni?.let {
                Log.d(TAG, "observePadiglioni: Nuova lista di padiglioni, size = ${padiglioni.size}")
                adapter.updateData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Mostra il dialog per la conferma del salvataggio dell'evento su calendario.
     *
     * @param esame L'esame da inserire.
     */
    private fun showConfermaMapsPadiglione(padiglione: Padiglione) {
        Log.d(TAG, "showConfermaMapsPadiglione: Mostro dialog per conferma maps del padiglione: ${padiglione.codice_padiglione}")
        val dialog = ConfirmMapsPadiglioneDialogFragment().apply {
            callback = { dialogResult ->
                if (dialogResult == "si") {
                    handleItemClick(padiglione)
                } else {
                    Toast.makeText(requireContext(), "Operazione annullata", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show(parentFragmentManager, "ConfirmMapsPadiglioneDialogFragment")
    }

    private fun handleItemClick(padiglione: Padiglione) {
        Toast.makeText(
            requireContext(),
            "Hai cliccato sull'item: ${padiglione.codice_padiglione}",
            Toast.LENGTH_LONG
        ).show()

        val posizione = padiglione.posizione
        if (posizione == null) {
            Toast.makeText(
                requireContext(),
                "Posizione non disponibile per il padiglione selezionato",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val lat= posizione.latitude
        val lng=posizione.longitude
        val codice=padiglione.codice_padiglione

        // Creazione URI per Google Maps
        // geo:lat,lng?q=lat,lng(Label)
        val gmmIntentUri = android.net.Uri.parse("geo:$lat,$lng?q=$lat,$lng(PADIGLIONE ${codice})")

        // Creazione Intent esplicito per Google Maps
        val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri).apply {
            // Impostare il pacchetto di Google Maps, in modo da aprire direttamente l'app,
            // se presente. In caso contrario, verrà mostrato un chooser con le app di mappe disponibili
            setPackage("com.google.android.apps.maps")
        }

        // Controllo se esiste un’app in grado di gestire l’Intent
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(
                requireContext(),
                "Nessuna app per le mappe trovata sul dispositivo!",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}