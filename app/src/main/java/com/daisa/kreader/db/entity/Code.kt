package com.daisa.kreader.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.daisa.kreader.CodeType

@Entity(tableName = "codes")
data class Code(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val text: String,
    val favorite: Boolean,
    val date: String,
    val type: CodeType


    )



