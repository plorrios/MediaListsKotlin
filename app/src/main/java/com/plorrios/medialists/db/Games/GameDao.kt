package com.plorrios.medialists.db.Games

import androidx.room.*

@Dao
interface GameDao {

    @Query("SELECT * FROM gamesItems WHERE title = :name")
    fun get(name: String): gamesItems

    @Query("SELECT * FROM gamesItems")
    fun getAll(): List<gamesItems>

    @Query("SELECT * FROM gamesItems WHERE list = :list")
    fun getOfList(list: String): List<gamesItems>

    @Update
    fun update(list: gamesItems)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg item: gamesItems)

    @Query("DELETE FROM gamesItems WHERE title = :item")
    fun delete(item: String)
    
}