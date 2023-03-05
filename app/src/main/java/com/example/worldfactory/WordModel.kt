package com.example.worldfactory

data class WordModel (
    var word: String,
    var phonetic: String,
    var phonetics: ArrayList<PhoneticsModel>,
    var meanings: ArrayList<MeaningModel>
)