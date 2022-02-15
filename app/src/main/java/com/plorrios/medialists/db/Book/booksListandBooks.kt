package com.plorrios.medialists.db.Book

import androidx.room.Embedded
import androidx.room.Relation

data class booksListandBooks (
    @Embedded
    val list: BooksListEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "list"
    )
    val games: List<booksItems>
)