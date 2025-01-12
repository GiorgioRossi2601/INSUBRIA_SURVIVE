package com.example.insubria_survive.ui.preferenze

import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreferenzeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inizializza il repository (usato da SQLite)
        val repository = LocalDbRepository(requireContext())
        // Ottieni l'username dell'utente attuale (da LoginRepository o altro)
        val username = LoginRepository.user?.username.toString()
        println(username)
        // Crea il ViewModel tramite la Factory
        val factory = PreferenzeViewModelFactory(repository, username)
        viewModel = ViewModelProvider(this, factory).get(preferenzeViewModel::class.java)

        setupRecyclerViews()
        observeViewModel()

        return root
    }

    private fun setupRecyclerViews() {
        adapterDaFare = PreferenzeAdapter(emptyList()) { /* eventuale callback */ }
        binding.rvDaFare.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDaFare.adapter = adapterDaFare

        adapterInForse = PreferenzeAdapter(emptyList()) { /* eventuale callback */ }
        binding.rvInForse.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInForse.adapter = adapterInForse

        adapterNonFare = PreferenzeAdapter(emptyList()) { /* eventuale callback */ }
        binding.rvNonFare.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNonFare.adapter = adapterNonFare
    }

    private fun observeViewModel() {
        viewModel.daFareList.observe(viewLifecycleOwner) { preferenze ->
            adapterDaFare.updatePreferenze(preferenze)
        }
        viewModel.inForseList.observe(viewLifecycleOwner) { preferenze ->
            adapterInForse.updatePreferenze(preferenze)
        }
        viewModel.nonFareList.observe(viewLifecycleOwner) { preferenze ->
            adapterNonFare.updatePreferenze(preferenze)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}