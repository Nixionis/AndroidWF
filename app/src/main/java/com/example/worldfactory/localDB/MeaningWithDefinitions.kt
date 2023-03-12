package com.example.worldfactory.localDB

import androidx.room.Embedded
import androidx.room.Relation

data class MeaningWithDefinitions (
    @Embedded val meaning: MeaningDBModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "meaningId"
    )
    val definitions: List<DefinitionDBModel>
)