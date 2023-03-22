package com.example.worldfactory

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.worldfactory.databinding.FragmentDictionaryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import android.graphics.Color
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.worldfactory.api.RetrofitApi
import com.example.worldfactory.api.WordModel
import com.example.worldfactory.localDB.WordWithPhonetics


/**
 * A simple [Fragment] subclass.
 * Use the [DictionaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private lateinit var recView: RecyclerView
    private lateinit var data: List<WordModel>
    private val dataAdapter: DataAdapter by lazy {
        DataAdapter(requireContext())
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.CardView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = dataAdapter
        }

        binding.buttonAddWord.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                (activity as WordActivity).saveWord(data)
                val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
                binding.buttonAddWord.startAnimation(animation)
                Handler().postDelayed({
                    binding.buttonAddWord.visibility = View.GONE
                }, 500)
            }
        }


        binding.texteditSearch.setOnKeyListener(View.OnKeyListener{ v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (!binding.texteditSearch.text.toString().isNullOrEmpty()) {

                    var word : List<WordWithPhonetics> = emptyList<WordWithPhonetics>()

                    viewLifecycleOwner.lifecycleScope.launch {

                        word = (activity as WordActivity).dao.getWordWithPhonetics(binding.texteditSearch.text.toString())

                        if (word.isEmpty()) {
                        //Need to get word from api
                            if ((activity as WordActivity).checkForInternet()) {

                                getWord(binding.texteditSearch.text.toString())

                            } else {

                                val dialogBuilder = AlertDialog.Builder(activity as WordActivity)
                                dialogBuilder.setTitle("Can't find your word!")
                                dialogBuilder.setMessage("We couldn't find your word from your dictionary and your connection is disabled.")
                                dialogBuilder.show()

                            }

                        } else {

                            //Finded in database -> show from db
                            getWordDB(word)
                        }
                    }

                }

                return@OnKeyListener true
            }
            false
        } )
        binding.loadCircle.visibility = View.GONE
        binding.CardView.visibility = View.GONE
        binding.buttonAddWord.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getWord(word : String){

        val api = Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/api/v2/entries/en/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitApi::class.java)

        activity?.runOnUiThread(java.lang.Runnable {
            binding.loadCircle.visibility = View.VISIBLE
            val animationIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            binding.loadCircle.startAnimation(animationIn)

            val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

            if(binding.buttonAddWord.visibility != View.GONE) {
                binding.buttonAddWord.startAnimation(animation)
            }

            binding.CardView.startAnimation(animation)

            Handler().postDelayed({
                binding.CardView.visibility = View.GONE
                binding.buttonAddWord.visibility = View.GONE
            }, 500)

        })

        GlobalScope.launch(Dispatchers.IO) {
            val response = api.word(word).awaitResponse()
            if (response.isSuccessful) {
                var cardList : ArrayList<WordListModel> = ArrayList()
                data = response.body()!!
                for (index in data.indices) {
                   // var headerCard = WordListModel.Header(data[word].word, )
                    var word: String = data[index].word
                    var phonetic: String = if(data[index].phonetic.isNullOrEmpty()) "" else data[index].phonetic
                    if(phonetic == "") {
                        if (data[index].phonetics.size != 0) {
                            for (indexp in data[index].phonetics.indices) {
                                if(!data[index].phonetics[indexp].text.isNullOrEmpty()) {
                                    phonetic = data[index].phonetics[indexp].text
                                    break
                                }
                            }
                        }
                    }


                    var audio: String = ""
                    if(audio == "") {
                        if (data[index].phonetics.size != 0) {
                            for (indexp in data[index].phonetics.indices) {
                                if(!data[index].phonetics[indexp].audio.isNullOrEmpty()) {
                                    audio = data[index].phonetics[indexp].audio
                                    break
                                }
                            }
                        }
                    }

                    cardList.add(WordListModel.Header(word, phonetic, audio))

                    for (jndex in data[index].meanings.indices){
                        cardList.add(WordListModel.PartOfSpeech(data[index].meanings[jndex].partOfSpeech))
                        cardList.add(WordListModel.MeaningHeader(true))

                        for(kndex in data[index].meanings[jndex].definitions.indices){
                            var def: String = data[index].meanings[jndex].definitions[kndex].definition
                            var example: Spannable = SpannableString("t")

                            if(!data[index].meanings[jndex].definitions[kndex].example.isNullOrEmpty()){
                                Log.d("AdapterView", "set example  = " + data[index].meanings[jndex].definitions[kndex].example)
                                val spannable: Spannable = SpannableString("Example: " + data[index].meanings[jndex].definitions[kndex].example)
                                spannable.setSpan(ForegroundColorSpan(Color.parseColor("#65AAEA")), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                example = spannable
                            }

                            cardList.add(WordListModel.DefinitionExample(def, example))
                        }
                    }
                }
               // Log.d("AdapterView", "set data to adapter = ${cardList.size}")
                activity?.runOnUiThread(java.lang.Runnable {
                    dataAdapter.setData(cardList)
                    binding.CardView.visibility = View.VISIBLE
                    binding.buttonAddWord.visibility = View.VISIBLE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                    binding.CardView.startAnimation(animation)
                    binding.buttonAddWord.startAnimation(animation)

                    val animationHide = AnimationUtils.loadAnimation(context, R.anim.fade_out)
                    binding.loadCircle.startAnimation(animationHide)
                    Handler().postDelayed({
                        binding.loadCircle.visibility = View.GONE
                    }, 500)
                })
            } else {
                activity?.runOnUiThread(java.lang.Runnable {
                    val dialogBuilder = AlertDialog.Builder(activity as WordActivity)
                    dialogBuilder.setTitle("Can't find your word!")
                    dialogBuilder.setMessage("There is no such a word in our world.")
                    dialogBuilder.show()
                })
            }
        }


    }

    private fun getWordDB(wordWithPhonetics : List<WordWithPhonetics>){

        if(binding.buttonAddWord.visibility != View.GONE) {

            activity?.runOnUiThread(java.lang.Runnable {
                //     binding.loadCircle.visibility = View.VISIBLE
                //    val animationIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                //    binding.loadCircle.startAnimation(animationIn)

                val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
                //    binding.CardView.startAnimation(animation)
                binding.buttonAddWord.startAnimation(animation)
                Handler().postDelayed({
                    //      binding.CardView.visibility = View.GONE
                    binding.buttonAddWord.visibility = View.GONE
                }, 500)

            })
        }

        //word = (activity as WordActivity).dao.getWordWithPhonetics(binding.texteditSearch.text.toString())

        GlobalScope.launch(Dispatchers.IO) {

            var cardList : ArrayList<WordListModel> = ArrayList()

            var word: String = wordWithPhonetics[0].word.word
            var phonetic: String = if(wordWithPhonetics[0].word.phonetic.isNullOrEmpty()) "" else wordWithPhonetics[0].word.phonetic
            if(phonetic == "") {
                if (wordWithPhonetics[0].phonetics.isNotEmpty()) {
                    for (indexp in wordWithPhonetics[0].phonetics.indices) {
                        if(!wordWithPhonetics[0].phonetics[indexp].text.isNullOrEmpty()) {
                            phonetic = wordWithPhonetics[0].phonetics[indexp].text
                            break
                        }
                    }
                }
            }

            var audio: String = ""
            if(audio == "") {
                if (wordWithPhonetics[0].phonetics.isNotEmpty()) {
                    for (indexp in wordWithPhonetics[0].phonetics.indices) {
                        if(!wordWithPhonetics[0].phonetics[indexp].audio.isNullOrEmpty()) {
                            audio = wordWithPhonetics[0].phonetics[indexp].audio
                            break
                        }
                    }
                }
            }

            cardList.add(WordListModel.Header(word, phonetic, audio))

            var meaningsList = (activity as WordActivity).dao.getWordWithMeanings(wordWithPhonetics[0].word.word)

            for (jndex in meaningsList[0].meanings.indices){
                cardList.add(WordListModel.PartOfSpeech(meaningsList[0].meanings[jndex].partOfSpeech))
                cardList.add(WordListModel.MeaningHeader(true))

                var definitionsList = (activity as WordActivity).dao.getMeaningWithDefinitions(meaningsList[0].meanings[jndex].id)

                for(kndex in definitionsList[0].definitions.indices){
                    var def: String = definitionsList[0].definitions[kndex].definition
                    var example: Spannable = SpannableString("t")

                    if(!definitionsList[0].definitions[kndex].example.isNullOrEmpty()){
                        val spannable: Spannable = SpannableString("Example: " + definitionsList[0].definitions[kndex].example)
                        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#65AAEA")), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        example = spannable
                    }

                    cardList.add(WordListModel.DefinitionExample(def, example))
                }
            }

            activity?.runOnUiThread(java.lang.Runnable {
                dataAdapter.setData(cardList)
                binding.CardView.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                binding.CardView.startAnimation(animation)

             //   val animationHide = AnimationUtils.loadAnimation(context, R.anim.fade_out)
             //   binding.loadCircle.startAnimation(animationHide)
             //   Handler().postDelayed({
             //       binding.loadCircle.visibility = View.GONE
             //   }, 500)
            })
        }
    }



}