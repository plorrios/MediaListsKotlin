package com.plorrios.medialists.db.Book

import androidx.room.*

@Dao
interface BookDao {

    @Query("SELECT * FROM booksItems WHERE title = :name")
    fun get(name: String): booksItems

    @Query("SELECT * FROM booksItems")
    fun getAll(): List<booksItems>

    @Query("SELECT * FROM booksItems WHERE list = :list")
    fun getOfList(list: String): List<booksItems>

    @Update
    fun update(list: booksItems)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg item: booksItems)

    @Query("DELETE FROM booksItems WHERE title = :item")
    fun delete(item: String)
    
}