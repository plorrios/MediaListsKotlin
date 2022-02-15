package com.plorrios.medialists.db.TV

import androidx.room.Embedded
import androidx.room.Relation

data class TVListandTV (
    @Embedded
    val list: TVListEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "list"
    )
    val tv: List<tvItems>
)