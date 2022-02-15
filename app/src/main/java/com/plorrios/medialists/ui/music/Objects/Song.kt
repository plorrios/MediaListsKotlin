package com.plorrios.medialists.ui.music.Objects

class Song (private var cName: String, private var cArtist: String, private var cUrl: String) {

    private var name = cName


    private var artist = cArtist

    private var url = cUrl


    fun getName(): String {
        return name
    }

    fun setName(Name: String){
        name = Name
    }

    fun getArtist(): String {
        return artist
    }

    fun setArtist(Artist: String){
        artist = Artist
    }

    fun getUrl(): String {
        return url
    }

    fun setUrl(Url: String){
        url = Url
    }

    override fun toString(): String {
        return "name: " + name + ", artist:" + artist
    }


}