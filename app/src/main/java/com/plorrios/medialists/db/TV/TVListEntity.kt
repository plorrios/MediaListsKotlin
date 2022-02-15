package com.plorrios.medialists.db.TV

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.plorrios.medialists.db.Music.MusicListEntity
import java.security.Key

@Entity
data class TVListEntity (
    @PrimaryKey
    var id: String,

)

@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = TVListEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("list"),
        onDelete = ForeignKey.CASCADE)
), tableName = "tvItems")
data class tvItems (

    @PrimaryKey
    val id: String,

    @ColumnInfo(index = true)
    var title: String,

    @ColumnInfo(index = true)
    var type: String,

    @ColumnInfo(index = true)
    val list: String

)