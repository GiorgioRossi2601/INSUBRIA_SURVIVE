package com.example.insubria_survive.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.insubria_survive.databinding.FragmentTimelineBinding

class timelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val timelineViewModel =
            ViewModelProvider(this).get(timelineViewModel::class.java)

        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.navTimeline
        timelineViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}