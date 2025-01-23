package com.example.insubria_survive.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.insubria_survive.R
import com.example.insubria_survive.databinding.FragmentHomeBinding

/**
 * Fragment per la visualizzazione della home.
 *
 * Mostra una griglia di sezioni, ciascuna naviga verso una specifica destinazione.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // La proprietà binding è valida solo tra onCreateView e onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: HomeAdapter

    // Definizione delle sezioni da mostrare nella home.
    private val sezioni = listOf(
        SezioneHome(R.drawable.ic_menu_exams, R.string.menu_esami, R.id.nav_exams),
        SezioneHome(R.drawable.ic_menu_lessons, R.string.menu_orario_lezioni, R.id.nav_lessons),
        SezioneHome(R.drawable.ic_menu_timeline, R.string.menu_timeline, R.id.nav_timeline),
        SezioneHome(R.drawable.ic_menu_preferences, R.string.menu_preferenze, R.id.nav_preferences)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    /**
     * Configura il RecyclerView impostando il layout a griglia, l'adapter e il listener per il click.
     */
    private fun setupRecyclerView() {
        binding.recyclerViewHome.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = HomeAdapter(sezioni).also { homeAdapter ->
                homeAdapter.setOnItemClickListener(object : HomeAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val sezione = sezioni[position]
                        val navController = findNavController()
                        // Se la destinazione è definita, crea NavOptions per fare popUpTo la destinazione home.
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.nav_home, inclusive = true)
                            .build()
                        sezione.destinatione?.let { destination ->
                            navController.navigate(destination, null, navOptions)
                        }
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
