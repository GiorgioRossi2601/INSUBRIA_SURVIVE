package com.example.insubria_survive.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.insubria_survive.data.db.LocalDbRepository

/**
 * Factory per la creazione di [TimelineViewModel].
 *
 * Permette di passare al ViewModel il repository.
 */

class TimelineViewModelFactory(
    private val repository: LocalDbRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
            return TimelineViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
