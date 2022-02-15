package com.plorrios.medialists.db.TV

import androidx.room.*
import com.plorrios.medialists.db.Music.musicItems

@Dao
interface TVListDao {

    @Query("SELECT * FROM tvlistentity WHERE id = :name")
    fun get(name: String): TVListEntity

    @Query("SELECT * FROM tvlistentity")
    fun getAll(): List<TVListEntity>

    @Update
    fun update(list: TVListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg list: TVListEntity)

    @Query("DELETE FROM tvlistentity WHERE id = :list")
    fun delete(list: String)

}