package com.plorrios.medialists.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.plorrios.medialists.db.Book.BookDao
import com.plorrios.medialists.db.Book.BooksListDao
import com.plorrios.medialists.db.Book.BooksListEntity
import com.plorrios.medialists.db.Book.booksItems
import com.plorrios.medialists.db.Games.GameDao
import com.plorrios.medialists.db.Games.GamesListDao
import com.plorrios.medialists.db.Games.GamesListEntity
import com.plorrios.medialists.db.Games.gamesItems
import com.plorrios.medialists.db.Music.MusicItemDao
import com.plorrios.medialists.db.Music.MusicListDao
import com.plorrios.medialists.db.Music.MusicListEntity
import com.plorrios.medialists.db.Music.musicItems
import com.plorrios.medialists.db.TV.TVItemDao
import com.plorrios.medialists.db.TV.TVListDao
import com.plorrios.medialists.db.TV.TVListEntity
import com.plorrios.medialists.db.TV.tvItems

@Database(entities = [MusicListEntity::class, GamesListEntity::class, BooksListEntity::class, TVListEntity::class, gamesItems::class, booksItems::class, musicItems::class, tvItems::class], version = 4)
@TypeConverters(Converters::class)
abstract class ListsDB : RoomDatabase(){

    abstract fun musicListDao(): MusicListDao
    abstract fun musicDao(): MusicItemDao
    abstract fun gamesListDao(): GamesListDao
    abstract fun gamesDao(): GameDao
    abstract fun booksListDao(): BooksListDao
    abstract fun booksDao(): BookDao
    abstract fun TVListDao(): TVListDao
    abstract fun TVDao(): TVItemDao

}
