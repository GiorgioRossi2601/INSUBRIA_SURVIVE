package com.example.insubria_survive.ui.preferenze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.insubria_survive.databinding.FragmentPreferenzeBinding
import com.example.insubria_survive.ui.esami.SharedEsameViewModel

class preferenzeFragment : Fragment() {

    private var _binding: FragmentPreferenzeBinding? = null
    private val binding get() = _binding!!

    // Otteniamo lo shared view model (activity-scoped)
    private val sharedEsameViewModel: SharedEsameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreferenzeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Osserva il LiveData per aggiornare la UI quando lo stato cambia
        sharedEsameViewModel.statoEsame.observe(viewLifecycleOwner) { nuovoStato ->
            binding.tvStatoEsame.text = "Stato Esame: " + nuovoStato.name.replace("_", " ").lowercase()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}