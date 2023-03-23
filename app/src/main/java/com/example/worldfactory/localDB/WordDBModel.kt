package com.example.worldfactory.localDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word")
data class WordDBModel (
    @PrimaryKey(autoGenerate = false)
    var word: String,
    var phonetic: String,
    var learncoef: Int = 0
)