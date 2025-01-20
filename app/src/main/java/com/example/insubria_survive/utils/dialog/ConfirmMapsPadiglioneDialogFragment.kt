package com.example.insubria_survive.utils.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * DialogFragment per confermare l'aggiunta di un evento su Google Calendar.
 *
 * Mostra un dialog "Yes/No" e invoca la callback passando la stringa "si" o "no" in base alla scelta dell'utente.
 */
class ConfirmMapsPadiglioneDialogFragment : DialogFragment() {
    /**
     * Callback che restituisce la risposta ("si" oppure "no").
     */
    var callback: ((String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Vuoi aprire Google Maps per andare al padiglione?")
            .setPositiveButton("Si") { _, _ ->
                callback?.invoke("si")
            }
            .setNegativeButton("No") { _, _ ->
                callback?.invoke("no")
            }
            .create()
    }
}
