package com.example.insubria_survive.ui.preferenze

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insubria_survive.data.LoginRepository
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.EsameConPreferenza
import com.example.insubria_survive.data.model.Stato
import com.example.insubria_survive.databinding.FragmentPreferenzeBinding
import com.example.insubria_survive.utils.dialog.CambiaStatoDialogFragment

/**
 * Fragment per la visualizzazione delle preferenze degli esami.
 *
 * Mostra tre RecyclerView per gli stati: Da Fare, In Forse, Non Fare.
 */
class PreferenzeFragment : Fragment() {

    private var _binding: FragmentPreferenzeBinding? = null
    private val binding get() = _binding!!

    // ViewModel "activity-scoped" per le preferenze
    private val viewModel: PreferenzeViewModel by activityViewModels {
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
        adapterDaFare = PreferenzeAdapter(emptyList()) { esameConPref ->
            showCambiaStatoDialog(esameConPref)
        }
        binding.rvDaFare.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDaFare.adapter = adapterDaFare

        adapterInForse = PreferenzeAdapter(emptyList()) { esameConPref ->
            showCambiaStatoDialog(esameConPref)
        }
        binding.rvInForse.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInForse.adapter = adapterInForse

        adapterNonFare = PreferenzeAdapter(emptyList()) { esameConPref ->
            showCambiaStatoDialog(esameConPref)
        }
        binding.rvNonFare.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNonFare.adapter = adapterNonFare
    }

    /**
     * Osserva le LiveData del ViewModel e aggiorna gli adapter.
     */
    private fun observeViewModel() {
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
        Log.d(
            TAG,
            "showCambiaStatoDialog: esame=${esameConPref.esame.id}, statoCorrente=${statoCorrente.name}"
        )

        val dialog =
            CambiaStatoDialogFragment.Companion.newInstance(esameConPref.esame, statoCorrente)
        dialog.show(parentFragmentManager, "CambiaStatoDialogFragment")
    }
}
