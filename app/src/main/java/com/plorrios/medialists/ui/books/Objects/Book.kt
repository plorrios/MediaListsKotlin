package com.plorrios.medialists.ui.books.Objects

class Book(private var cVolumeInfo: VolumeInfo, private var cId: String) {

    private var volumeInfo = cVolumeInfo

    private var id = cId


    fun getInfo(): VolumeInfo {
        return volumeInfo
    }

    fun setInfo(Title: VolumeInfo){
        volumeInfo = Title
    }

    fun getId(): String {
        return id
    }

    fun setId(Id: String){
        id = Id
    }

    override fun toString(): String {
        return volumeInfo.toString() + ":" + id
    }
}

class VolumeInfo(private var cTitle: String){

    private var title: String = cTitle

    fun getTitle(): String {
        return title
    }

    fun setTitle(Title: String) {
        title = Title
    }
}