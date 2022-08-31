package com.daisa.kreader.db.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daisa.kreader.db.entity.Code
import com.daisa.kreader.db.repository.CodeRepository
import kotlinx.coroutines.launch

class CodeViewModel(val repository: CodeRepository): ViewModel() {


    fun insert(code: Code) = viewModelScope.launch {
        repository.insert(code)
    }

    fun getAll() = viewModelScope.launch {
        repository.getAll()
    }

    fun getCodeByText(text: String) = viewModelScope.launch {
        repository.getCodeByText(text)
    }
}

class CodeViewModelFactory(private val repository: CodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CodeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CodeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}