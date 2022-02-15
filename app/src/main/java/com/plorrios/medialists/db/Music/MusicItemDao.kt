package com.plorrios.medialists.db.Music

import androidx.room.*

@Dao
interface MusicItemDao {
    
    @Query("SELECT * FROM musicItems WHERE title = :name")
    fun get(name: String): musicItems

    @Query("SELECT * FROM musicItems")
    fun getAll(): List<musicItems>

    @Query("SELECT * FROM musicItems WHERE list = :list AND artist = :artist")
    fun getOfList(list: String, artist: String): List<musicItems>

    @Update
    fun update(list: musicItems)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg item: musicItems)

    @Query("DELETE FROM musicItems WHERE title = :item AND artist = :artist")
    fun delete(item: String, artist: String)
    
}