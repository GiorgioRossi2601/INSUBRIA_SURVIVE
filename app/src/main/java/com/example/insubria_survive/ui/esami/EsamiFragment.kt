package com.example.insubria_survive.ui.esami

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insubria_survive.data.LoginRepository
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Stato
import com.example.insubria_survive.databinding.FragmentEsamiBinding
import com.example.insubria_survive.ui.preferenze.CambiaStatoDialogFragment

/**
 * Fragment per la visualizzazione degli esami.
 */
class EsamiFragment : Fragment() {

    // Tag per il logging
    companion object {
        private const val TAG = "EsamiFragment"
    }

    // Binding per il layout del Fragment
    private var _binding: FragmentEsamiBinding? = null
    private val binding get() = _binding!!

    private lateinit var esamiAdapter: EsamiAdapter
    private lateinit var esamiViewModel: EsamiViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: inizializzazione del fragment")

        // Inizializzazione del repository locale e del ViewModel tramite factory
        val repository = LocalDbRepository(requireContext())
        val factory = EsamiViewModelFactory(repository)
        esamiViewModel = ViewModelProvider(this, factory).get(EsamiViewModel::class.java)

        _binding = FragmentEsamiBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    /**
     * Configura il RecyclerView e l'adapter per la lista di esami.
     */
    private fun setupRecyclerView() {
        esamiAdapter = EsamiAdapter(emptyList()) { esame ->
            showEsameStatusDialog(esame)
        }
        binding.recyclerViewEsami.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = esamiAdapter
        }
        Log.d(TAG, "RecyclerView configurato correttamente")

        // Imposta il listener per il click sull'intero item della RecyclerView
        esamiAdapter.setOnItemClickListener(object : EsamiAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                // Utilizzo del metodo getEsameAt per recuperare direttamente l'Esame
                val esameSelezionato = esamiAdapter.getEsameAt(position)
                Toast.makeText(
                    requireContext(),
                    "Hai cliccato sull'item: ${esameSelezionato.corso}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "Click sull'item: ${esameSelezionato.id}")
                // Eventuali ulteriori azioni (es. navigazione o aggiornamenti)
            }
        })
    }

    /**
     * Osserva le modifiche alla lista di esami e aggiorna l'UI di conseguenza.
     */
    private fun observeViewModel() {
        esamiViewModel.esamiList.observe(viewLifecycleOwner) { esami ->
            if (!esami.isNullOrEmpty()) {
                Log.d(TAG, "Esami caricati: ${esami.size}")
                esamiAdapter.updateData(esami)
            } else {
                Log.d(TAG, "Lista esami vuota")
                Toast.makeText(requireContext(), "Nessun esame trovato.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView: binding nulled")
    }

    /**
     * Mostra il dialog per la modifica dello stato dell'esame, basandosi sulla preferenza salvata.
     *
     * @param esame L'esame selezionato.
     */
    private fun showEsameStatusDialog(esame: Esame) {
        Log.d(TAG, "Mostro dialog per lo stato dell'esame: ${esame.id}")

        val repository = LocalDbRepository(requireContext())
        // Ottiene l'username dell'utente loggato (se disponibile)
        val username = LoginRepository.user?.username.orEmpty()
        // Recupera la preferenza esistente, se presente
        val preferenza = repository.getPreferenzaByEsameAndUser(esame.id, username)
        val statoCorrente = preferenza?.stato?.let { Stato.valueOf(it) } ?: Stato.IN_FORSE

        // Mostra il dialog con lo stato corrente
        val dialog = CambiaStatoDialogFragment.newInstance(esame, statoCorrente)
        dialog.show(parentFragmentManager, "CambiaStatoDialogFragment")
    }
}
