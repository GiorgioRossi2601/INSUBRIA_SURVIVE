package com.example.insubria_survive.ui.esami

import android.os.Bundle
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
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Stato
import com.example.insubria_survive.databinding.FragmentEsamiBinding
import com.example.insubria_survive.ui.preferenze.CambiaStatoDialogFragment

class EsamiFragment : Fragment() {

    private var _binding: FragmentEsamiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var esamiAdapter: EsamiAdapter
    private lateinit var esamiViewModel: EsamiViewModel

    // Ottieni lo shared view model in ambito Activity
    private val sharedEsameViewModel: SharedEsameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        println("Inizializzazione EsamiFragment")
        val repository = LocalDbRepository(requireContext())
        val factory = EsamiViewModelFactory(repository)
        esamiViewModel =
            ViewModelProvider(this, factory).get(EsamiViewModel::class.java)

        _binding = FragmentEsamiBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        esamiAdapter = EsamiAdapter(emptyList()) { esame ->
            showEsameStatusDialog(esame)
        }
        binding.recyclerViewEsami.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = esamiAdapter
        }
    }

    private fun observeViewModel() {
        esamiViewModel.esamiList.observe(viewLifecycleOwner) { esami ->
            if (!esami.isNullOrEmpty()) {
                println("Esami caricati nel Fragment: ${esami.size}")
                esamiAdapter.updateData(esami)
            } else {
                println("Lista esami vuota.")
                Toast.makeText(requireContext(), "Nessun esame trovato.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun EsamiFragment.showEsameStatusDialog(esame: Esame) {
        val repository = LocalDbRepository(requireContext())
        val username = LoginRepository.user?.username.toString()
        // Recupera la preferenza esistente, se presente
        val preferenza = repository.getPreferenzaByEsameAndUser(esame.id, username)
        val statoCorrente = preferenza?.stato?.let { Stato.valueOf(it) } ?: Stato.IN_FORSE
        val dialog = CambiaStatoDialogFragment.newInstance(esame, statoCorrente)
        dialog.show(parentFragmentManager, "CambiaStatoDialogFragment")

    }
}


