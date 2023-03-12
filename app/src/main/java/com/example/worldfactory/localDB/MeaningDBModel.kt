package com.example.worldfactory.localDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meanings")
data class MeaningDBModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val word: String,
    var partOfSpeech: String,
    //var definitions: ArrayList<DefinitionModel>)
)