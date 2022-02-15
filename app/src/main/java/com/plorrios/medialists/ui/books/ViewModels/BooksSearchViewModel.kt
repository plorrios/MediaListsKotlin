package com.plorrios.medialists.ui.books.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plorrios.medialists.ui.books.Objects.Book
import com.plorrios.medialists.ui.games.Objects.Game

class BooksSearchViewModel : ViewModel()
{
    // This list will keep all values for Your ListView
    private val _list: MutableLiveData<ArrayList<Book>> = MutableLiveData()
    val list: LiveData<ArrayList<Book>>
        get() = _list
    private val _query: MutableLiveData<String> = MutableLiveData()
    val query: LiveData<String>
        get() = _query

    init
    {
        _list.value = ArrayList()
    }

    // function which will add new values to Your list
    fun addNewItems(argList: ArrayList<Book>)
    {
        _list.value!!.addAll(argList)
    }

    fun setNewItems(argList: ArrayList<Book>)
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