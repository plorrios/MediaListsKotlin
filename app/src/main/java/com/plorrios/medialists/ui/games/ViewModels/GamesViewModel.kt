package com.plorrios.medialists.ui.games.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GamesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Games Fragment"
    }
    val text: LiveData<String> = _text
}