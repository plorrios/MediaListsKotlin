package com.plorrios.medialists.db.Games

import androidx.room.*

@Entity(tableName = "gameslistentity")
data class GamesListEntity (
    @PrimaryKey
    var id: String,
)

@Entity(foreignKeys = arrayOf(ForeignKey(entity = GamesListEntity::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("list"),
    onDelete = ForeignKey.CASCADE)),
    tableName = "gamesItems")
data class gamesItems (

    @PrimaryKey
    val id: String,

    @ColumnInfo(index = true)
    val title: String,

    @ColumnInfo(index = true)
    val list: String

)