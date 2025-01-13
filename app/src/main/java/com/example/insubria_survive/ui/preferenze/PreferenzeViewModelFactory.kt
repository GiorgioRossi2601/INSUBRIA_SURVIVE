package com.example.insubria_survive.ui.preferenze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.insubria_survive.data.db.LocalDbRepository

/**
 * Factory per la creazione del preferenzeViewModel.
 *
 * Permette di passare al ViewModel il repository e l'username dell'utente.
 */
class PreferenzeViewModelFactory(
    private val repository: LocalDbRepository,
    private val username: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(preferenzeViewModel::class.java)) {
            return preferenzeViewModel(repository, username) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
