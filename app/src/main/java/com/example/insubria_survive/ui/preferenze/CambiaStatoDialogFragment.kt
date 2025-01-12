package com.example.insubria_survive.ui.preferenze

import android.app.Dialog
import android.os.Bundle
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

class CambiaStatoDialogFragment : DialogFragment() {
    companion object {
        private const val ARG_ESAME_ID = "esame_codice"

        fun newInstance(esame: Esame): CambiaStatoDialogFragment {
            val fragment = CambiaStatoDialogFragment()
            val args = Bundle()
            args.putString(ARG_ESAME_ID, esame.id)
            fragment.arguments = args
            return fragment
        }
    }

    // Ottieni lo shared view model (activity-scoped)
    private val sharedEsameViewModel: SharedEsameViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val esame = arguments?.getString(ARG_ESAME_ID) ?: ""

        // Otteniamo i valori dell'enum Stato
        val stati = Stato.values()
        // Formattiamo i nomi (ad esempio: "Da Fare", "In Forse", "Non Fare")
        val statoNames = stati.map { it.name.replace("_", " ").lowercase()}.toTypedArray()

        var selectedIndex = 1

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cambia Stato")
            .setSingleChoiceItems(statoNames, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setMessage("Seleziona lo stato per l'esame $esame:")
            .setPositiveButton("Conferma") { dialog, _ ->
                if (selectedIndex != -1) {
                    val statoSelezionato = stati[selectedIndex]

                    // Aggiorna lo shared view model con il nuovo stato
                    sharedEsameViewModel.setStatoEsame(statoSelezionato)
                    // Qui potresti anche aggiungere una logica per aggiornare il DB, se necessario.

                    // Se vuoi salvare la preferenza nel DB, qui puoi richiamare il repository per aggiornare la tabella preferenze_esame.
                    // Ad esempio:
                    val username = LoginRepository.user?.username.toString()
                    val preferenza = Preferenza(null, esame, username, statoSelezionato.name)
                    LocalDbRepository(requireContext()).insertOrUpdatePreferenza(preferenza)

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

