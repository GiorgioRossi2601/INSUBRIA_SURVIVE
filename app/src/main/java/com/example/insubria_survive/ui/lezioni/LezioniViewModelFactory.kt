package com.example.insubria_survive.ui.lezioni

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory per la creazione del [LezioniViewModel].
 *
 * Consente di passare il [Context] per l'accesso al database locale.
 */
class LezioniViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LezioniViewModel::class.java)) {
            return LezioniViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
