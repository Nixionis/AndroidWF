package com.example.worldfactory

import android.text.Spannable

sealed class WordListModel {
    data class Header(
        var word: String,
        var phonetic: String,
        var audio: String
    ) : WordListModel()

    data class PartOfSpeech(
        var partOfSpeech: String
    ) : WordListModel()

    data class MeaningHeader(
        var show : Boolean
    ) : WordListModel()

    data class DefinitionExample(
        var definition: String,
        var example: Spannable
    ) : WordListModel()
}