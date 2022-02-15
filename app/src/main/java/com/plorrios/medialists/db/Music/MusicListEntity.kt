package com.plorrios.medialists.db.Music

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "musiclistentity")
data class MusicListEntity (
    @PrimaryKey
    var id: String,
)

@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = MusicListEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("list"),
        onDelete = ForeignKey.CASCADE)),
    tableName = "musicItems")
data class musicItems (

    @PrimaryKey
    val title: String,

    @ColumnInfo(index = true)
    val artist: String,

    @ColumnInfo(index = true)
    val list: String

)