package com.example.insubria_survive.ui.preferenze

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insubria_survive.data.LoginRepository
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.EsameConPreferenza
import com.example.insubria_survive.data.model.Stato
import com.example.insubria_survive.databinding.FragmentPreferenzeBinding

/**
 * Fragment per la visualizzazione delle preferenze degli esami.
 * Mostra tre RecyclerView per i diversi stati: Da Fare, In Forse, Non Fare.
 */
class preferenzeFragment : Fragment() {

    private var _binding: FragmentPreferenzeBinding? = null
    private val binding get() = _binding!!

    // ViewModel "activity-scoped" ottenuto tramite una factory (passando repository e username)
    private val viewModel: preferenzeViewModel by activityViewModels {
        val repository = LocalDbRepository(requireContext())
        Log.d(TAG, "onCreateView: Repository inizializzato")
        val username = LoginRepository.user?.username.toString()
        Log.d(TAG, "onCreateView: Username ottenuto: $username")
        PreferenzeViewModelFactory(repository, username)
    }

    // Adapter per le RecyclerView
    private lateinit var adapterDaFare: PreferenzeAdapter
    private lateinit var adapterInForse: PreferenzeAdapter
    private lateinit var adapterNonFare: PreferenzeAdapter

    // Tag per il logging
    companion object {
        private const val TAG = "PreferenzeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: INIZIALIZZAZIONE PREFERENZE FRAGMENT")
        _binding = FragmentPreferenzeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerViews()
        observeViewModel()

        Log.d(TAG, "onCreateView: FINE creazione view")
        return root
    }

    /**
     * Configura le RecyclerView e i rispettivi adapter.
     */
    private fun setupRecyclerViews() {
        Log.d(TAG, "setupRecyclerViews: Impostazione adapter e layout manager per rvDaFare")
        adapterDaFare = PreferenzeAdapter(emptyList()) { esameConPref ->
            // Al click su un item, mostra il dialog per cambiare stato
            showCambiaStatoDialog(esameConPref)
        }
        binding.rvDaFare.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDaFare.adapter = adapterDaFare

        Log.d(TAG, "setupRecyclerViews: Impostazione adapter e layout manager per rvInForse")
        adapterInForse = PreferenzeAdapter(emptyList()) { esameConPref ->
            showCambiaStatoDialog(esameConPref)
        }
        binding.rvInForse.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInForse.adapter = adapterInForse

        Log.d(TAG, "setupRecyclerViews: Impostazione adapter e layout manager per rvNonFare")
        adapterNonFare = PreferenzeAdapter(emptyList()) { esameConPref ->
            showCambiaStatoDialog(esameConPref)
        }
        binding.rvNonFare.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNonFare.adapter = adapterNonFare
    }

    /**
     * Osserva i LiveData del ViewModel e aggiorna gli adapter.
     */
    private fun observeViewModel() {
        Log.d(TAG, "observeViewModel: Inizio osservazione LiveData")
        viewModel.daFareList.observe(viewLifecycleOwner) { preferenze ->
            Log.d(TAG, "observeViewModel: Ricevuta lista DaFare con ${preferenze.size} elementi")
            adapterDaFare.updatePreferenze(preferenze)
        }
        viewModel.inForseList.observe(viewLifecycleOwner) { preferenze ->
            Log.d(TAG, "observeViewModel: Ricevuta lista InForse con ${preferenze.size} elementi")
            adapterInForse.updatePreferenze(preferenze)
        }
        viewModel.nonFareList.observe(viewLifecycleOwner) { preferenze ->
            Log.d(TAG, "observeViewModel: Ricevuta lista NonFare con ${preferenze.size} elementi")
            adapterNonFare.updatePreferenze(preferenze)
        }
    }

    // Ricarica i dati quando il fragment torna visibile
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Richiamo loadPreferenze()")
        viewModel.loadPreferenze()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: View distrutta")
        _binding = null
    }

    /**
     * Mostra il dialog per cambiare lo stato dell'esame selezionato.
     *
     * @param esameConPref Oggetto contenente l'esame e lo stato attuale.
     */
    private fun showCambiaStatoDialog(esameConPref: EsameConPreferenza) {
        val repository = LocalDbRepository(requireContext())
        val username = LoginRepository.user?.username.toString()
        // Recupera la preferenza esistente, se presente
        val preferenza = repository.getPreferenzaByEsameAndUser(esameConPref.esame.id, username)
        val statoCorrente = preferenza?.stato?.let { Stato.valueOf(it) } ?: Stato.IN_FORSE
        Log.d(TAG, "showCambiaStatoDialog: esame=${esameConPref.esame.id}, statoCorrente=${statoCorrente.name}")

        // Crea e mostra il dialog
        val dialog = CambiaStatoDialogFragment.newInstance(esameConPref.esame, statoCorrente)
        dialog.show(parentFragmentManager, "CambiaStatoDialogFragment")
    }
}
