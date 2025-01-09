package com.example.insubria_survive.ui.preferenze

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.insubria_survive.data.model.Esame
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CambiaStatoDialogFragment : DialogFragment() {
    companion object {
        private const val ARG_ESAME_ID = "esame_id"

        fun newInstance(esame: Esame): CambiaStatoDialogFragment {
            val fragment = CambiaStatoDialogFragment()
            val args = Bundle()
            args.putString(ARG_ESAME_ID, esame.id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val esameId = arguments?.getString(ARG_ESAME_ID)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cambia Stato")
            .setMessage("Imposta lo stato per l'esame con ID: $esameId")
            .setPositiveButton("Confermato") { _, _ ->
                // Logica per aggiornare lo stato dell'esame
                println("Stato impostato su Confermato per esame $esameId")
            }
            .setNegativeButton("Annullato") { _, _ -> }
            .create()
    }

}