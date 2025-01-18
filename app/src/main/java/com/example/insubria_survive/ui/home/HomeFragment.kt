package com.example.insubria_survive.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.insubria_survive.databinding.FragmentHomeBinding
import com.example.insubria_survive.R
import com.example.insubria_survive.data.model.SezioneHome

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: HomeAdapter

    private val sezioni = listOf(
        SezioneHome(R.drawable.ic_menu_exams, R.string.menu_esami, R.id.nav_exams),
        SezioneHome(R.drawable.ic_menu_lessons, R.string.menu_orario_lezioni, R.id.nav_lessons),
        SezioneHome(R.drawable.ic_menu_timeline, R.string.menu_timeline, R.id.nav_timeline),
        SezioneHome(R.drawable.ic_menu_navigator, R.string.menu_navigatore, R.id.nav_navigator),
        SezioneHome(R.drawable.ic_menu_preferences, R.string.menu_preferenze, R.id.nav_preferences)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewHome

        recyclerView.layoutManager = GridLayoutManager(context, 3)

        adapter = HomeAdapter(sezioni)
        adapter.setOnItemClickListener(object : HomeAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val sezione = sezioni[position]
                val navController = findNavController()
                // Se la destinazione è definita, crea NavOptions per fare popUpTo la home destination
                val navOptions = NavOptions.Builder()
                    // 'R.id.nav_home' è il punto di partenza
                    .setPopUpTo(R.id.nav_home, inclusive = true)
                    .build()
                navController.navigate(sezione.destinatione!!, null, navOptions)
            }
        })

        recyclerView.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}