package com.plorrios.medialists.db.Music

import androidx.room.Embedded
import androidx.room.Relation

data class musicListandMusic(
    @Embedded
    val list: MusicListEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "list"
    )
    val games: List<musicItems>
)