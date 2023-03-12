package com.example.worldfactory.localDB

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WordDBModel::class,
        PhoneticsDBModel::class,
        MeaningDBModel::class,
        DefinitionDBModel::class
    ],
    version = 1
)
abstract class DictionaryDatabaseNew: RoomDatabase(){

    abstract val wordDao: WordDao

    companion object{
        @Volatile
        private var INSTANCE: DictionaryDatabaseNew? = null

        fun getInstance(context: Context): DictionaryDatabaseNew {

         
            synchronized(this){
                return  INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DictionaryDatabaseNew::class.java,
                    "dictionarynew_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}