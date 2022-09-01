package com.daisa.kreader.db.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.daisa.kreader.Constants
import com.daisa.kreader.db.entity.Code
import com.daisa.kreader.db.repository.CodeRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CodeViewModel(val repository: CodeRepository) : ViewModel() {


    suspend fun insert(code: Code) {
        Log.d(Constants.TAG, "insert I'm working in thread ${Thread.currentThread().name}")
        repository.insert(code)

    }

    fun existsCode(text: String): Int {
        var result = -1
        runBlocking {

            val job =
                viewModelScope.launch {
                    Log.d(
                        Constants.TAG,
                        "existsCode I'm working in thread ${Thread.currentThread().name}"
                    )
                    result = repository.existsCode(text)
                }
            job.join()

        }

        return result
    }

    val allCodes: LiveData<List<Code>> = repository.getAll().asLiveData()
}

