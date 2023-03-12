package com.example.worldfactory.localDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phonetics")
data class PhoneticsDBModel (
    val id: Int,
    val word: String,
    var text: String,
    @PrimaryKey(autoGenerate = false)
    var audio: String,
)