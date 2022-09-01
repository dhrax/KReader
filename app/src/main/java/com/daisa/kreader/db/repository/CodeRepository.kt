package com.daisa.kreader.db.repository

import androidx.annotation.WorkerThread
import com.daisa.kreader.db.dao.CodeDao
import com.daisa.kreader.db.entity.Code
import kotlinx.coroutines.flow.Flow

class CodeRepository(private val codeDao: CodeDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(code: Code) {
        codeDao.insert(code)
    }

    @WorkerThread
    fun getAll() : Flow<List<Code>> {
        return codeDao.getAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun existsCode(text: String) : Int{
        return codeDao.existsCode(text)
    }
}