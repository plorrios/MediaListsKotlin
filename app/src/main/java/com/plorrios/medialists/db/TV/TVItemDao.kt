package com.plorrios.medialists.db.TV

import androidx.room.*

@Dao
interface TVItemDao {

    @Query("SELECT * FROM tvItems WHERE title = :name")
    fun get(name: String): tvItems

    @Query("SELECT * FROM tvItems")
    fun getAll(): List<tvItems>

    @Query("SELECT * FROM tvItems WHERE list = :list")
    fun getOfList(list: String): List<tvItems>

    @Update
    fun update(list: tvItems)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg item: tvItems)

    @Query("DELETE FROM tvItems WHERE title = :item")
    fun delete(item: String)
    
}