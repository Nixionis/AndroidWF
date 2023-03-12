package com.example.worldfactory.localDB

import android.text.Spannable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "definitions")
data class DefinitionDBModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val meaningId: Int,
    var definition: String,
    var example: String
    )