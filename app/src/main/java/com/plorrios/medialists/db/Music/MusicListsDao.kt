package com.plorrios.medialists.db.Music

import androidx.room.*

@Dao
interface MusicListDao {

    @Query("SELECT * FROM musiclistentity WHERE id = :name")
    fun get(name: String): MusicListEntity

    @Query("SELECT * FROM musicItems WHERE title = :title AND artist = :artist")
    fun getItem(title: String, artist: String): musicItems

    @Query("SELECT * FROM musiclistentity")
    fun getAll(): List<MusicListEntity>

    @Update
    fun update(list: MusicListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg list: MusicListEntity)

    @Query("DELETE FROM musiclistentity WHERE id = :list")
    fun delete(list: String)

}