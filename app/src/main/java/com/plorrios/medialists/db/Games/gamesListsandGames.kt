package com.plorrios.medialists.db.Games

import androidx.room.Embedded
import androidx.room.Relation

data class gamesListsandGames (
    @Embedded
    val list: GamesListEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "list"
    )
    val games: List<gamesItems>
)