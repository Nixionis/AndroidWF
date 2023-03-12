package com.example.worldfactory.localDB

import androidx.room.*


@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordDBModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhonetic(phonetic: PhoneticsDBModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeaning(meaning: MeaningDBModel) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefinition(definition: DefinitionDBModel)

    @Transaction
    @Query("SELECT * FROM word WHERE word = :wordName")
    suspend fun getWordWithPhonetics(wordName: String): List<WordWithPhonetics>

    @Transaction
    @Query("SELECT * FROM word WHERE word = :wordName")
    suspend fun getWordWithMeanings(wordName: String): List<WordWithMeanings>

    @Transaction
    @Query("SELECT * FROM meanings WHERE id = :meanId")
    suspend fun getMeaningWithDefinitions(meanId: Int): List<MeaningWithDefinitions>

}