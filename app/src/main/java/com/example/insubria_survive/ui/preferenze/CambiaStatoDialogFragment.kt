package com.example.insubria_survive.ui.preferenze

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.insubria_survive.data.LoginRepository
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Preferenza
import com.example.insubria_survive.data.model.Stato
import com.example.insubria_survive.ui.esami.SharedEsameViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.currentCoroutineContext
import java.util.Locale

class CambiaStatoDialogFragment : DialogFragment() {
    companion object {
        private const val ARG_ESAME_ID = "esame_codice"
        private const val ARG_STATO = "stato"

        fun newInstance(esame: Esame, statoCorrente: Stato? = Stato.IN_FORSE): CambiaStatoDialogFragment {
            val fragment = CambiaStatoDialogFragment()
            val args = Bundle()
            args.putString(ARG_ESAME_ID, esame.id)
            args.putString(ARG_STATO, statoCorrente?.name ?: Stato.IN_FORSE.name)
            fragment.arguments = args
            return fragment
        }
    }

    // Ottieni lo shared view model (activity-scoped)
    private val sharedEsameViewModel: SharedEsameViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val esame = arguments?.getString(ARG_ESAME_ID) ?: ""
        // Recupera lo stato corrente passato come parametro
        val nomeStatoCorrente = arguments?.getString(ARG_STATO) ?: Stato.IN_FORSE.name

        // Otteniamo i valori dell'enum Stato
        val stati = Stato.values()
        val nomeStati = stati.map {
            it.name.replace("_", " ").lowercase(Locale.getDefault())
                .replaceFirstChar { ch -> ch.uppercaseChar() }
        }.toTypedArray()

        var selectedIndex = stati.indexOfFirst { it.name == nomeStatoCorrente }
        if (selectedIndex < 0) selectedIndex = 1

        // Usando un ArrayAdapter con un layout built-in per single choice
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_single_choice,
            nomeStati
        )

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cambia Stato dell'Esame $esame:")
            .setSingleChoiceItems(adapter, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Conferma") { dialog, _ ->
                if (selectedIndex != -1) {
                    val statoSelezionato = stati[selectedIndex]

                    val username = LoginRepository.user?.username.toString()
                    val repository = LocalDbRepository(requireContext())
                    // Recupera l'esistente, se presente
                    val existingPref = repository.getPreferenzaByEsameAndUser(esame, username)
                    val preferenza = if (existingPref != null) {
                        //Preferenza(existingPref.id, esame, username, statoSelezionato.name)
                        existingPref.copy(stato = statoSelezionato.name)
                    } else {
                        Preferenza(null, esame, username, statoSelezionato.name)
                    }

                    repository.insertOrUpdatePreferenza(preferenza)
                    sharedEsameViewModel.setStatoEsame(statoSelezionato)

                    // Mostra il messaggio informativo:
                    val message = "L'utente $username ha scelto lo stato ${statoSelezionato.name} per l'esame ${esame}"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

                }
            }
            .setNegativeButton("Annulla") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

}


