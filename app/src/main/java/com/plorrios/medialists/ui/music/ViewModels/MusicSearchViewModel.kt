package com.plorrios.medialists.ui.music.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plorrios.medialists.ui.games.Objects.Game
import com.plorrios.medialists.ui.music.Objects.Song

class MusicSearchViewModel: ViewModel()
{
    // This list will keep all values for Your ListView
    private val _list: MutableLiveData<ArrayList<Song>> = MutableLiveData()
    val list: LiveData<ArrayList<Song>>
        get() = _list

    init
    {
        _list.value = ArrayList()
    }

    // function which will add new values to Your list
    fun addNewItems(argList: ArrayList<Song>)
    {
        _list.value!!.addAll(argList)
    }
}