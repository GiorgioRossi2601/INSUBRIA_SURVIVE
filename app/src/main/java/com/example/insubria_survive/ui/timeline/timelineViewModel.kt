package com.example.insubria_survive.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class timelineViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Timeline Fragment"
    }
    val text: LiveData<String> = _text
}