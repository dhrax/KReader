package com.daisa.kreader.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.daisa.kreader.CodeType
import com.daisa.kreader.db.dao.CodeDao
import com.daisa.kreader.db.entity.Code
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Code::class], version = 1)
abstract class CodeDatabase : RoomDatabase() {
    abstract fun codeDao(): CodeDao

    //singleton implementation as a RoomDatabase has a high creation cost and it is rare to need more than one database per applications
    companion object {
        @Volatile
        private var INSTANCE: CodeDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CodeDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CodeDatabase::class.java,
                    "code_database"
                ).addCallback(CodeDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


    private class CodeDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.codeDao())
                }
            }
        }

        suspend fun populateDatabase(codeDao: CodeDao) {
            // Delete all content here.
            codeDao.deleteAll()

            val code1 = Code(1, "Texto leido 1", false, "20222008", CodeType.URL)
            val code2 = Code(2, "Texto leido 2", false, "20223108", CodeType.WIFI)

            codeDao.insert(code1)
            codeDao.insert(code2)


        }
    }
}