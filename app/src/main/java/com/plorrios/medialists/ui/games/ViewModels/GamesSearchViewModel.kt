package com.plorrios.medialists.ui.games.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plorrios.medialists.ui.TV.Objects.TVObjects
import com.plorrios.medialists.ui.games.Objects.Game

class GamesSearchViewModel : ViewModel()
{
    // This list will keep all values for Your ListView
    private val _list: MutableLiveData<ArrayList<Game>> = MutableLiveData()
    val list: LiveData<ArrayList<Game>>
        get() = _list
    private val _query: MutableLiveData<String> = MutableLiveData()
    val query: LiveData<String>
        get() = _query

    init
    {
        _list.value = ArrayList()
    }

    // function which will add new values to Your list
    fun addNewItems(argList: Array<Game>)
    {
        _list.value!!.addAll(argList)
    }

    fun setNewItems(argList: Array<Game>)
    {
        _list.value!!.clear()
        _list.value!!.addAll(argList)
    }

    fun setQuery(argQuery: String?){
        _query.value = argQuery!!
    }

    fun clear(){
        _query.value = ""
        _list.value!!.clear()
    }
}