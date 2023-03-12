package com.example.worldfactory.localDB

import androidx.room.Embedded
import androidx.room.Relation

data class WordWithPhonetics (
    @Embedded val word: WordDBModel,
    @Relation(
        parentColumn = "word",
        entityColumn = "word"
    )
    val phonetics: List<PhoneticsDBModel>
)