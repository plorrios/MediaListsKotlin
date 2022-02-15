package com.plorrios.medialists.ui.games.Objects


class Game(private var cName: String, private var cId: Int) {

    private var name = cName

    private var id = cId

    fun getName(): String {
        return name
    }

    fun setName(Name: String){
        name = Name
    }

    fun getId(): Int {
        return id
    }

    fun setId(Id: Int){
        id = Id
    }

    override fun toString(): String {
        return name + id
    }


}