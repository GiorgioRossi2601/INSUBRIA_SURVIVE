package com.example.insubria_survive.utils.dialog

import android.R
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.insubria_survive.data.LoginRepository
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Preferenza
import com.example.insubria_survive.data.model.Stato
import com.example.insubria_survive.ui.preferenze.PreferenzeViewModel
import com.example.insubria_survive.ui.preferenze.PreferenzeViewModelFactory
import com.example.insubria_survive.utils.UtilsMethod
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

/**
 * DialogFragment per la modifica dello stato di un [Esame].
 * Viene mostrato un dialog "single choice" per la selezione dello stato.
 */
class CambiaStatoDialogFragment : DialogFragment() {

    companion object {
        private const val TAG = "CambiaStatoDialogFragment"
        private const val ARG_ESAME_ID = "esame_codice"
        private const val ARG_STATO = "stato"
        private const val ARG_CORSO = "corso"
        private const val ARG_DATA = "data"

        /**
         * Crea una nuova istanza del dialog per modificare lo stato di un [Esame].
         *
         * @param esame L'oggetto [Esame] di cui modificare lo stato.
         * @param statoCorrente Stato attuale (default [Stato.IN_FORSE]).
         */
        fun newInstance(esame: Esame, statoCorrente: Stato? = Stato.IN_FORSE): CambiaStatoDialogFragment {
            val fragment = CambiaStatoDialogFragment()
            val args = Bundle().apply {
                putString(ARG_ESAME_ID, esame.id)
                putString(ARG_STATO, statoCorrente?.name ?: Stato.IN_FORSE.name)
                putString(ARG_CORSO, esame.corso)
                putString(ARG_DATA, UtilsMethod().firebaseTimestampToDateLongFormat(esame.data!!))
            }
            fragment.arguments = args
            return fragment
        }
    }

    // ViewModel "activity-scoped" per la gestione delle preferenze
    private val preferenzeViewModel: PreferenzeViewModel by activityViewModels {
        val repository = LocalDbRepository(requireContext())
        val username = LoginRepository.user?.username.orEmpty()
        PreferenzeViewModelFactory(repository, username)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Estrae l'id dell'esame e lo stato corrente dai parametri
        val esameId = arguments?.getString(ARG_ESAME_ID) ?: ""
        val corso = arguments?.getString(ARG_CORSO) ?: ""
        val data = arguments?.getString(ARG_DATA) ?: ""
        val nomeStatoCorrente = arguments?.getString(ARG_STATO) ?: Stato.IN_FORSE.name
        Log.d(TAG, "onCreateDialog: esameId=$esameId, corso=$corso, statoCorrente=$nomeStatoCorrente")

        // Ottiene tutti i possibili stati dall'enum [Stato]
        val stati = Stato.values()
        val nomeStati = stati.map { stato ->
            stato.name.replace("_", " ")
                .lowercase(Locale.getDefault())
                .replaceFirstChar { ch -> ch.uppercaseChar() }
        }.toTypedArray()

        // Seleziona l'indice dell'attuale stato
        var selectedIndex = stati.indexOfFirst { it.name == nomeStatoCorrente }
        if (selectedIndex < 0) selectedIndex = 1

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_list_item_single_choice,
            nomeStati
        )

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("$corso - $data")
            .setSingleChoiceItems(adapter, selectedIndex) { _, which ->
                selectedIndex = which
                Log.d(TAG, "Stato selezionato: ${stati[selectedIndex].name}")
            }
            .setPositiveButton("Conferma") { dialog, _ ->
                if (selectedIndex != -1) {
                    val statoSelezionato = stati[selectedIndex]
                    Log.d(TAG, "Conferma stato: ${statoSelezionato.name}")

                    val username = LoginRepository.user?.username.toString()
                    val repository = LocalDbRepository(requireContext())
                    val existingPref = repository.getPreferenzaByEsameAndUser(esameId, username)

                    // Se esiste, aggiorna la preferenza, altrimenti creane una nuova
                    val preferenza = if (existingPref != null) {
                        existingPref.copy(stato = statoSelezionato.name)
                    } else {
                        Preferenza(null, esameId, username, statoSelezionato.name)
                    }
                    repository.insertOrUpdatePreferenza(preferenza)
                    Log.d(TAG, "Preferenza salvata: $preferenza")

                    // Aggiorna il ViewModel per ricaricare la lista delle preferenze
                    preferenzeViewModel.loadPreferenze()

                    val message = "Preferenza aggiornata"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Annulla") { dialog, _ ->
                Log.d(TAG, "Azione Annulla eseguita")
                val message = "Operazione annullata"
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
            .create()
    }
}
