package com.plorrios.medialists.db.Book

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "bookslistentity")
data class BooksListEntity (
    @PrimaryKey
    var id: String,
)


@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = BooksListEntity::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("list"),
    onDelete = ForeignKey.CASCADE)
), tableName = "booksItems")
data class booksItems (

    @PrimaryKey
    val id: String,

    @ColumnInfo(index = true)
    val title: String,

    @ColumnInfo(index = true)
    val list: String

)