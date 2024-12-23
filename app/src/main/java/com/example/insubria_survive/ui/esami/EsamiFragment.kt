package com.example.insubria_survive.ui.esami

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.insubria_survive.databinding.FragmentEsamiBinding

class EsamiFragment : Fragment() {

    private var _binding: FragmentEsamiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val esamiViewModel =
            ViewModelProvider(this).get(EsamiViewModel::class.java)

        _binding = FragmentEsamiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.navExams
        esamiViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}