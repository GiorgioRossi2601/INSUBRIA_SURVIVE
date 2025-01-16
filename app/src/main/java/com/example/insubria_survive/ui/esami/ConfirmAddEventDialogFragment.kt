package com.example.insubria_survive.ui.esami

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.insubria_survive.data.model.Esame


class ConfirmAddEventDialogFragment: DialogFragment() {
    // Callback da invocare alla scelta dell'utente
    var callback: ((String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Vuoi salvare evento sul Google Calendar?")
            .setPositiveButton("Si") { _, _ ->
                callback?.invoke("si")
            }
            .setNegativeButton("No") { _, _ ->
                callback?.invoke("no")
            }
            .create()
    }
}