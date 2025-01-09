package com.example.insubria_survive.ui.esami

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.databinding.FragmentEsamiBinding
import com.example.insubria_survive.ui.preferenze.CambiaStatoDialogFragment

class EsamiFragment : Fragment() {

    private var _binding: FragmentEsamiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var esamiAdapter: EsamiAdapter
    private lateinit var esamiViewModel: EsamiViewModel

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
        val dialogView = CambiaStatoDialogFragment.Companion.newInstance(esame)
        dialogView.show(parentFragmentManager, "CambiaStatoDialogFragment")

    }
}


