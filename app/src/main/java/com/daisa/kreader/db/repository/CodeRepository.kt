package com.daisa.kreader.db.repository

import androidx.annotation.WorkerThread
import com.daisa.kreader.db.dao.CodeDao
import com.daisa.kreader.db.entity.Code

class CodeRepository(private val codeDao: CodeDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(code: Code) {
        codeDao.insert(code)
    }

    @WorkerThread
    suspend fun getAll() : List<Code> {
        return codeDao.getAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getCodeByText(text: String) : Int{
        return codeDao.getCodeByText(text)
    }
}