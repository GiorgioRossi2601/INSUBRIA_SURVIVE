package com.example.insubria_survive.ui.lezioni

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class lezioniViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Lezioni Fragment"
    }
    val text: LiveData<String> = _text
}