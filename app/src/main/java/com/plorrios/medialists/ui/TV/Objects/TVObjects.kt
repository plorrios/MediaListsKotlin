package com.plorrios.medialists.ui.TV.Objects

sealed class TVObjects {
    data class Movie(
        val title: String,
        val id : Int,
    ) : TVObjects()
    data class Series(
        val name: String,
        val id : Int,
    ) : TVObjects()
}