package com.example.insubria_survive.ui.esami

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.insubria_survive.data.db.LocalDbRepository

/**
 * Factory per la creazione del ViewModel degli esami.
 *
 * Consente di passare il repository locale al ViewModel.
 */
class EsamiViewModelFactory(
    private val repository: LocalDbRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EsamiViewModel::class.java)) {
            Log.d("EsamiViewModelFactory", "create: Creazione di EsamiViewModel")
            return EsamiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
