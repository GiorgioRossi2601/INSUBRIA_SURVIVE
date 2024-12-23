package com.example.insubria_survive.ui.navigatore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class navigatoreViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Navigatore Fragment"
    }
    val text: LiveData<String> = _text
}