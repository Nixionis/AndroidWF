package com.example.worldfactory.api

data class MeaningModel (
    var partOfSpeech: String,
    var definitions: ArrayList<DefinitionModel>)