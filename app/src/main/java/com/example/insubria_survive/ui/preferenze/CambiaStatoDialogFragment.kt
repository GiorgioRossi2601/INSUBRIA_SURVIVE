package com.example.insubria_survive.ui.preferenze

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.insubria_survive.data.LoginRepository
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Preferenza
import com.example.insubria_survive.data.model.Stato
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

/**
 * DialogFragment per la modifica dello stato di un Esame.
 * Utilizza un dialog "single choice" per permettere la selezione dello stato.
 */
class CambiaStatoDialogFragment : DialogFragment() {

    companion object {
        //Tag per il logging
        private const val TAG = "CambiaStatoDialogFragment"
        private const val ARG_ESAME_ID = "esame_codice"
        private const val ARG_STATO = "stato"

        /**
         * Crea una nuova istanza del dialog passando l'id dell'esame e lo stato corrente.
         *
         * @param esame L'oggetto Esame per cui modificare lo stato.
         * @param statoCorrente Stato attuale, default a IN_FORSE se non specificato.
         */
        fun newInstance(esame: Esame, statoCorrente: Stato? = Stato.IN_FORSE): CambiaStatoDialogFragment {
            val fragment = CambiaStatoDialogFragment()
            val args = Bundle()
            args.putString(ARG_ESAME_ID, esame.id)
            args.putString(ARG_STATO, statoCorrente?.name ?: Stato.IN_FORSE.name)
            fragment.arguments = args
            return fragment
        }
    }

    // Ottiene lo shared ViewModel (con scope Activity) per aggiornare le preferenze
    private val preferenzeViewModel: preferenzeViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Estrae dall'argomento l'id dell'esame e lo stato corrente (default "IN_FORSE")
        val esameId = arguments?.getString(ARG_ESAME_ID) ?: ""
        val nomeStatoCorrente = arguments?.getString(ARG_STATO) ?: Stato.IN_FORSE.name
        Log.d(TAG, "onCreateDialog: esameId=$esameId, statoCorrente=$nomeStatoCorrente")

        // Ottiene tutti i possibili stati dall'enum Stato
        val stati = Stato.values()
        val nomeStati = stati.map { stato ->
            stato.name.replace("_", " ")
                .lowercase(Locale.getDefault())
                .replaceFirstChar { ch -> ch.uppercaseChar() }
        }.toTypedArray()

        // Seleziona l'indice dell'attuale stato nella lista degli stati
        var selectedIndex = stati.indexOfFirst { it.name == nomeStatoCorrente }
        if (selectedIndex < 0) selectedIndex = 1

        // Crea un ArrayAdapter per il dialog con layout built-in (single choice)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_single_choice,
            nomeStati
        )

        // Costruisce e restituisce il MaterialAlertDialog
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cambia Stato dell'Esame $esameId:")
            .setSingleChoiceItems(adapter, selectedIndex) { _, which ->
                // Aggiorna l'indice selezionato al click
                selectedIndex = which
                Log.d(TAG, "Stato selezionato: ${stati[selectedIndex].name}")
            }
            .setPositiveButton("Conferma") { dialog, _ ->
                if (selectedIndex != -1) {
                    val statoSelezionato = stati[selectedIndex]
                    Log.d(TAG, "Conferma stato: ${statoSelezionato.name}")

                    // Recupera username dall'utente loggato
                    val username = LoginRepository.user?.username.toString()
                    // Crea un'istanza del repository locale
                    val repository = LocalDbRepository(requireContext())
                    // Cerca eventuale preferenza esistente per l'esame e l'utente
                    val existingPref = repository.getPreferenzaByEsameAndUser(esameId, username)

                    // Se esiste, aggiorna lo stato; altrimenti crea una nuova preferenza
                    val preferenza = if (existingPref != null) {
                        existingPref.copy(stato = statoSelezionato.name)
                    } else {
                        Preferenza(null, esameId, username, statoSelezionato.name)
                    }
                    // Salva la preferenza nel DB locale
                    repository.insertOrUpdatePreferenza(preferenza)
                    Log.d(TAG, "Preferenza salvata: $preferenza")

                    // Aggiorna il ViewModel per far ricaricare la lista di preferenze
                    preferenzeViewModel.loadPreferenze()

                    // Mostra un Toast informativo all'utente
                    val message = "Preferenza aggiunta con successo! Stato: ${statoSelezionato.name.replace("_", " ").uppercase(Locale.getDefault())}"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Annulla") { dialog, _ ->
                Log.d(TAG, "Azione Annulla eseguita")
                dialog.dismiss()
            }
            .create()
    }
}
