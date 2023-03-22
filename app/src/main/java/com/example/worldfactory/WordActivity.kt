package com.example.worldfactory

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.worldfactory.api.WordModel
import com.example.worldfactory.databinding.ActivityWordBinding
import com.example.worldfactory.localDB.*

class WordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordBinding
    private lateinit var viewPager: ViewPager2
    lateinit var dao: WordDao
    lateinit var mediaPlayer: MediaPlayer
    var currentTab : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dao = DictionaryDatabaseNew.getInstance(this).wordDao

        viewPager = binding.viewpagermenu
        viewPager.isUserInputEnabled = false
        viewPager.adapter = PagerAdapterMainMenu(this)

        binding.bottomMenu?.setOnItemSelectedListener {

            //Switch between fragments
            when (it.itemId) {
                R.id.dictionary -> {
                    viewPager.setCurrentItem(0, true)
                    currentTab = 0
                    return@setOnItemSelectedListener true
                }
                R.id.training -> {
                    viewPager.setCurrentItem(1, true)
                    currentTab = 1
                    return@setOnItemSelectedListener true
                }
                R.id.video -> {
                    viewPager.setCurrentItem(2, true)
                    currentTab = 2
                    return@setOnItemSelectedListener true
                }
            }
            false
        }

        mediaPlayer = MediaPlayer()
    }

    fun checkForInternet(): Boolean{

        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    suspend fun saveWord(wordData: List<WordModel>) {

                for (index in wordData.indices) {

                    var word: String = wordData[index].word
                    var phonetic: String = if(wordData[index].phonetic.isNullOrEmpty()) "" else wordData[index].phonetic
                    if(phonetic == "") {
                        if (wordData[index].phonetics.size != 0) {
                            for (indexp in wordData[index].phonetics.indices) {
                                if(!wordData[index].phonetics[indexp].text.isNullOrEmpty()) {
                                    phonetic = wordData[index].phonetics[indexp].text
                                    break
                                }
                            }
                        }
                    }

                    dao.insertWord(WordDBModel(word, phonetic))

                    var audio: String = ""
                    if(audio == "") {
                        if (wordData[index].phonetics.size != 0) {
                            for (indexp in wordData[index].phonetics.indices) {
                                if(!wordData[index].phonetics[indexp].audio.isNullOrEmpty()) {
                                    audio = wordData[index].phonetics[indexp].audio
                                    break
                                }
                            }
                        }
                    }

                    dao.insertPhonetic(PhoneticsDBModel(0, word, phonetic, audio))

                    for (jndex in wordData[index].meanings.indices){
                        var meanId: Long = 0
                            meanId = dao.insertMeaning(MeaningDBModel(0, word, wordData[index].meanings[jndex].partOfSpeech))

                        for(kndex in wordData[index].meanings[jndex].definitions.indices){
                            var def: String = wordData[index].meanings[jndex].definitions[kndex].definition
                            var example: String = ""

                            if(!wordData[index].meanings[jndex].definitions[kndex].example.isNullOrEmpty()){
                                example = "Example: " + wordData[index].meanings[jndex].definitions[kndex].example
                            }

                            dao.insertDefinition(DefinitionDBModel(0, meanId.toInt(), def, example))

                        }
                    }
                }
    }

    fun playAudio(url: String){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

        if (checkForInternet()) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } else {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Can't play audio!")
            dialogBuilder.setMessage("It seems that your connection is disabled.")
            dialogBuilder.show()
        }
    }
}