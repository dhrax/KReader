package com.daisa.kreader.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.daisa.kreader.db.entity.Code

@Dao
interface CodeDao {

    @Insert
    fun insertAll(vararg codes: Code)

    @Insert
    fun insert(code: Code)

    @Delete
    fun deleteCode(code: Code)

    @Query("SELECT * FROM codes")
    suspend fun getAll() : List<Code>

    @Query("DELETE FROM codes")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM codes where text = :text limit 1")
    suspend fun getCodeByText(text: String) : Int
}