package com.plorrios.medialists.db.Games

import androidx.room.*

@Dao
interface GamesListDao {

    @Query("SELECT * FROM gameslistentity WHERE id = :name")
    fun get(name: String): GamesListEntity

    @Query("SELECT * FROM gamesItems WHERE id = :name")
    fun getItem(name: String): gamesItems

    @Query("SELECT * FROM gameslistentity")
    fun getAll(): List<GamesListEntity>

    @Update
    fun update(list: GamesListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg list: GamesListEntity)

    @Query("DELETE FROM gameslistentity WHERE id = :list")
    fun delete(list: String)

}