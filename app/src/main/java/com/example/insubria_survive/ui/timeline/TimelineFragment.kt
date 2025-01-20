package com.example.insubria_survive.ui.timeline

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.databinding.FragmentTimelineBinding

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
}