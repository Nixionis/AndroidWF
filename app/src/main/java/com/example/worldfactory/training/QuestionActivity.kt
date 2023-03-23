package com.example.worldfactory.training

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import com.example.worldfactory.databinding.ActivityQuestionBinding
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.lifecycleScope
import com.example.worldfactory.R
import com.example.worldfactory.intro.IntroActivity
import com.example.worldfactory.localDB.DictionaryDatabaseNew
import com.example.worldfactory.localDB.WordDBModel
import com.example.worldfactory.localDB.WordDao
import com.example.worldfactory.localDB.WordWithMeanings
import kotlinx.coroutines.*

class QuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionBinding
    private var prevButtonIndex: Int = -1
    private lateinit var words : List<WordWithMeanings>
    private lateinit var wrongwords : List<WordDBModel>
    private var definitions : ArrayList<String> = ArrayList()
    private var correctIndices: ArrayList<Int>  = ArrayList()
    private lateinit var dao: WordDao
    private var currentQuestionPosition = 0
    private lateinit var countTimer: Job
    private var correctAmount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dao = DictionaryDatabaseNew.getInstance(this).wordDao

        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBackQuestion.setOnClickListener {
            super.onBackPressed()
        }

        binding.buttonAnswer1.setOnClickListener {
            answerClick(0)
        }

        binding.buttonAnswer2.setOnClickListener {
            answerClick(1)
        }

        binding.buttonAnswer3.setOnClickListener {
            answerClick(2)
        }

        binding.buttonAgain.setOnClickListener {
            val intent = Intent(this@QuestionActivity, QuestionActivity::class.java)
            startActivity(intent)
            finish()
        }

        fetchQuestions()
    }

    private fun fetchQuestions() {
        this.lifecycleScope.launch {

            words = dao.getLowestCoefWords()

            if(words.size < 3) {
                //generate template words
            }

            for(i in words.indices) {
                var rMean = words[i].meanings[(0 until words[i].meanings.size).random()]
                var poses = dao.getMeaningWithDefinitions(rMean.id)
                var def = poses[0].definitions[(0 until poses[0].definitions.size).random()].definition
                definitions.add(def)

                correctIndices.add((0..2).random())
            }

            //Add wrong answers
            wrongwords = dao.getRandomWords()
        }

        Handler().postDelayed({
            createQuestion()
        }, 1050)
    }

    private fun createQuestion(){
        binding.textCurrentQuestion.text =  "${(currentQuestionPosition+1).toString()} of ${words.size}"
        binding.textDefinition.text = definitions[currentQuestionPosition]

        binding.textAnswer1.text = if(correctIndices[currentQuestionPosition] == 0) words[currentQuestionPosition].word.word else getRandomWrongAnswer(words[currentQuestionPosition].word.word)
        binding.textAnswer2.text = if(correctIndices[currentQuestionPosition] == 1) words[currentQuestionPosition].word.word else getRandomWrongAnswer(words[currentQuestionPosition].word.word)
        binding.textAnswer3.text = if(correctIndices[currentQuestionPosition] == 2) words[currentQuestionPosition].word.word else getRandomWrongAnswer(words[currentQuestionPosition].word.word)

        this.runOnUiThread(java.lang.Runnable {
            binding.textCurrentQuestion.visibility = View.VISIBLE
            binding.textDefinition.visibility = View.VISIBLE
            binding.buttonAnswer1.visibility = View.VISIBLE
            binding.buttonAnswer2.visibility = View.VISIBLE
            binding.buttonAnswer3.visibility = View.VISIBLE

            val animationIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_fast)
            binding.textCurrentQuestion.startAnimation(animationIn)
            binding.textDefinition.startAnimation(animationIn)
            binding.buttonAnswer1.startAnimation(animationIn)
            binding.buttonAnswer2.startAnimation(animationIn)
            binding.buttonAnswer3.startAnimation(animationIn)

            if(binding.progressBar.visibility != View.VISIBLE) {
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.startAnimation(animationIn)
            }

            val animationOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_fast)
            binding.loadCircle2.startAnimation(animationOut)
            Handler().postDelayed({
                binding.loadCircle2.visibility = View.GONE
            }, 250)

            if(currentQuestionPosition > 0) {
                var animator = ObjectAnimator.ofInt(binding.progressBar, "progress", (currentQuestionPosition-1) * (1000f / words.size).toInt(), currentQuestionPosition * (1000f / words.size).toInt())
                animator.startDelay = 0
                animator.duration = 500
                animator.repeatCount = 0
                animator.interpolator = DecelerateInterpolator()
                animator.start()
            }
        })

        startQuestionTimer()

    }

    private fun getRandomWrongAnswer(correctWord: String): String {
        if(wrongwords.size < 3){
            var randomWords:ArrayList<String> = ArrayList()

            randomWords.add("boom")
            randomWords.add("book")
            randomWords.add("galaxy")
            randomWords.add("cooking")
            randomWords.add("smiling")
            randomWords.add("house")
            randomWords.add("key")
            randomWords.add("cable")
            randomWords.add("test")
            randomWords.add("flash")
            randomWords.add("bash")
            randomWords.add("mash")
            randomWords.add("mutiny")
            randomWords.add("space")
            randomWords.add("build")

            var randomWord = correctWord
            while(randomWord == correctWord) {
                randomWord = randomWords[(0..14).random()]
            }

            return randomWord
        } else {
            var randomWord = correctWord
            while(randomWord == correctWord) {
                randomWord = wrongwords[(0 until wrongwords.size).random()].word
            }
            return randomWord
        }
    }

    private fun answerClick(answerIndex: Int){

        var button = if(answerIndex == 0) binding.buttonAnswer1 else if (answerIndex == 1) binding.buttonAnswer2 else binding.buttonAnswer3
        var prevbutton = if(prevButtonIndex == 0) binding.buttonAnswer1 else if (prevButtonIndex == 1) binding.buttonAnswer2 else if (prevButtonIndex == 2) binding.buttonAnswer3 else null
        prevButtonIndex = answerIndex

        this.runOnUiThread(java.lang.Runnable {
            var pvhWidth = PropertyValuesHolder.ofInt("strokeWidth", 3, 6)
            //animator.interpolator = DecelerateInterpolator()

            var pvhStrokeColor = PropertyValuesHolder.ofInt("strokeColor", Color.parseColor("#BEBAB3"), Color.parseColor("#F3705A"))
            pvhStrokeColor.setEvaluator(ArgbEvaluator())

            var pvhBackgroundColor = PropertyValuesHolder.ofInt("cardBackgroundColor", Color.parseColor("#FFFFFFFF"), Color.parseColor("#33F3705A"))
            pvhBackgroundColor.setEvaluator(ArgbEvaluator())

            var animator = ObjectAnimator.ofPropertyValuesHolder(button, pvhWidth, pvhStrokeColor, pvhBackgroundColor)
            animator.startDelay = 0
            animator.duration = 200
            animator.repeatCount = 0
            animator.start()

            if (prevbutton != null && button != prevbutton){
                var pvhWidth = PropertyValuesHolder.ofInt("strokeWidth", 6, 3)
                //animator.interpolator = DecelerateInterpolator()

                var pvhStrokeColor = PropertyValuesHolder.ofInt("strokeColor", Color.parseColor("#F3705A"), Color.parseColor("#BEBAB3"))
                pvhStrokeColor.setEvaluator(ArgbEvaluator())

                var pvhBackgroundColor = PropertyValuesHolder.ofInt("cardBackgroundColor", Color.parseColor("#33F3705A"), Color.parseColor("#FFFFFFFF"))
                pvhBackgroundColor.setEvaluator(ArgbEvaluator())

                var animator = ObjectAnimator.ofPropertyValuesHolder(prevbutton, pvhWidth, pvhStrokeColor, pvhBackgroundColor)
                animator.startDelay = 0
                animator.duration = 200
                animator.repeatCount = 0
                animator.start()
            }
        })

    }

    private fun checkAnswer(){
       // Log.d("TrainingTest", words[currentQuestionPosition].word.word)
        //Log.d("TrainingTest", "$prevButtonIndex and ${correctIndices[currentQuestionPosition]}")
       // Log.d("TrainingTest", "$currentQuestionPosition")
        var dbposition = currentQuestionPosition

        if(prevButtonIndex == correctIndices[currentQuestionPosition]) {
            correctAmount++
            GlobalScope.launch {
                dao.addPointToWord(words[dbposition].word.word)
            }
        } else {
            GlobalScope.launch {
                dao.subtructPointToWord(words[dbposition].word.word)
            }
        }
        currentQuestionPosition++
       // Log.d("TrainingTest", "$currentQuestionPosition and ${words.size}")
        if(currentQuestionPosition == words.size) {
            showResults()
        } else {
            createQuestion()
        }
    }

    private fun showResults() {
        binding.textCorrect.text = "Correct: $correctAmount"
        binding.textIncorrect.text = "Incorrect: ${words.size - correctAmount}"

        this.runOnUiThread(java.lang.Runnable {

            binding.imageView7.visibility = View.VISIBLE
            binding.textFinish.visibility = View.VISIBLE
            binding.textCorrect.visibility = View.VISIBLE
            binding.textIncorrect.visibility = View.VISIBLE
            binding.buttonAgain.visibility = View.VISIBLE

            val animationIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_fast)
            binding.imageView7.startAnimation(animationIn)
            binding.textFinish.startAnimation(animationIn)
            binding.textCorrect.startAnimation(animationIn)
            binding.textIncorrect.startAnimation(animationIn)
            binding.buttonAgain.startAnimation(animationIn)

            var animator = ObjectAnimator.ofInt(binding.progressBar, "progress", (currentQuestionPosition-1) * (1000f / words.size).toInt(), currentQuestionPosition * (1000f / words.size).toInt())
            animator.startDelay = 0
            animator.duration = 500
            animator.repeatCount = 0
            animator.interpolator = DecelerateInterpolator()
            animator.start()

        })
    }

    private fun resetAnswerButtons(){
        var prevbutton = if(prevButtonIndex == 0) binding.buttonAnswer1 else if (prevButtonIndex == 1) binding.buttonAnswer2 else if (prevButtonIndex == 2) binding.buttonAnswer3 else null
        prevButtonIndex = -1

        if(prevbutton != null) {
            prevbutton.strokeWidth = 3
            prevbutton.strokeColor = Color.parseColor("#BEBAB3")
            prevbutton.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
        }
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private fun startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long = 0, action: () -> Unit) =scope.launch(
        Dispatchers.IO) {
        delay(delayMillis)
        if (repeatMillis > 0) {
            while (isActive) {
                action()
                delay(repeatMillis)
            }
        } else {
            action()
        }
    }

    private fun startQuestionTimer(){

        countTimer = startCoroutineTimer(5000, 5000) {
            this.runOnUiThread(java.lang.Runnable {

                val animationIn = AnimationUtils.loadAnimation(this, R.anim.fade_out_fast)
                binding.textCurrentQuestion.startAnimation(animationIn)
                binding.textDefinition.startAnimation(animationIn)
                binding.buttonAnswer1.startAnimation(animationIn)
                binding.buttonAnswer2.startAnimation(animationIn)
                binding.buttonAnswer3.startAnimation(animationIn)

                Handler().postDelayed({
                    binding.textCurrentQuestion.visibility = View.GONE
                    binding.textDefinition.visibility = View.GONE
                    binding.buttonAnswer1.visibility = View.GONE
                    binding.buttonAnswer2.visibility = View.GONE
                    binding.buttonAnswer3.visibility = View.GONE
                }, 250)

            })
        }

        this.lifecycleScope.launch {

            delay(5300)
            countTimer.cancelAndJoin()

            checkAnswer()
            resetAnswerButtons()
        }
    }
}