package com.example.insubria_survive.ui.esami

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EsamiViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Esami Fragment"
    }
    val text: LiveData<String> = _text
}