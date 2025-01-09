package com.example.insubria_survive.ui.esami

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.insubria_survive.data.db.LocalDbRepository

class EsamiViewModelFactory(
    private val repository: LocalDbRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EsamiViewModel::class.java)) {
            return EsamiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}