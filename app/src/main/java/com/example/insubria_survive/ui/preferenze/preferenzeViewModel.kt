package com.example.insubria_survive.ui.preferenze

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class preferenzeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Preferenze Fragment"
    }
    val text: LiveData<String> = _text
}