package com.plorrios.medialists.db.Book

import androidx.room.*

@Dao
interface BooksListDao {

    @Query("SELECT * FROM bookslistentity WHERE id = :name")
    fun get(name: String): BooksListEntity

    @Query("SELECT * FROM booksItems WHERE id = :name")
    fun getItem(name: String): booksItems

    @Query("SELECT * FROM bookslistentity")
    fun getAll(): List<BooksListEntity>

    @Update
    fun update(list: BooksListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg list: BooksListEntity)

    @Query("DELETE FROM bookslistentity WHERE id = :list")
    fun delete(list: String)

}