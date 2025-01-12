package com.example.insubria_survive.ui.preferenze

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insubria_survive.data.LoginRepository
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.databinding.FragmentPreferenzeBinding

class preferenzeFragment : Fragment() {

    private var _binding: FragmentPreferenzeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: preferenzeViewModel

    // Adapter per ciascuna RecyclerView (per oggetti Preferenza)
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

        // Inizializza il repository (usato da SQLite)
        val repository = LocalDbRepository(requireContext())
        Log.d(TAG, "onCreateView: Repository inizializzato")
        // Ottieni l'username dell'utente attuale (da LoginRepository o altro)
        val username = LoginRepository.user?.username.toString()
        Log.d(TAG, "onCreateView: Username ottenuto: $username")
        // Crea il ViewModel tramite la Factory
        val factory = PreferenzeViewModelFactory(repository, username)
        viewModel = ViewModelProvider(this, factory).get(preferenzeViewModel::class.java)
        Log.d(TAG, "onCreateView: ViewModel creato")

        setupRecyclerViews()
        observeViewModel()

        Log.d(TAG, "onCreateView: FINE creazione view")
        return root
    }

    private fun setupRecyclerViews() {
        Log.d(TAG, "setupRecyclerViews: Impostazione adapter e layout manager per rvDaFare")
        adapterDaFare = PreferenzeAdapter(emptyList()) { /* eventuale callback */ }
        binding.rvDaFare.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDaFare.adapter = adapterDaFare

        Log.d(TAG, "setupRecyclerViews: Impostazione adapter e layout manager per rvInForse")
        adapterInForse = PreferenzeAdapter(emptyList()) { /* eventuale callback */ }
        binding.rvInForse.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInForse.adapter = adapterInForse

        Log.d(TAG, "setupRecyclerViews: Impostazione adapter e layout manager per rvNonFare")
        adapterNonFare = PreferenzeAdapter(emptyList()) { /* eventuale callback */ }
        binding.rvNonFare.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNonFare.adapter = adapterNonFare
    }

    private fun observeViewModel() {
        Log.d(TAG, "observeViewModel: Inizio osservazione LiveData")
        viewModel.daFareList.observe(viewLifecycleOwner) { preferenze ->
            Log.d(TAG, "observeViewModel: Ricevuta lista daFare con ${preferenze.size} elementi")
            adapterDaFare.updatePreferenze(preferenze)
        }
        viewModel.inForseList.observe(viewLifecycleOwner) { preferenze ->
            Log.d(TAG, "observeViewModel: Ricevuta lista inForse con ${preferenze.size} elementi")
            adapterInForse.updatePreferenze(preferenze)
        }
        viewModel.nonFareList.observe(viewLifecycleOwner) { preferenze ->
            Log.d(TAG, "observeViewModel: Ricevuta lista nonFare con ${preferenze.size} elementi")
            adapterNonFare.updatePreferenze(preferenze)
        }
    }

    // Aggiunta la ricarica dei dati quando il fragment torna visibile
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
}