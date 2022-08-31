package com.daisa.kreader

import android.app.Application
import com.daisa.kreader.db.CodeDatabase
import com.daisa.kreader.db.repository.CodeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class QRApplication : Application(){

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { CodeDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { CodeRepository(database.codeDao()) }
}