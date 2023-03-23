package com.example.worldfactory

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.lifecycleScope
import com.example.worldfactory.databinding.FragmentTrainingBinding
import com.example.worldfactory.login.LoginActivity
import com.example.worldfactory.training.QuestionActivity
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TrainingFragment : Fragment() {

    private var _binding: FragmentTrainingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTrainingBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkWordCount()
        super.onViewCreated(view, savedInstanceState)

        //    binding.buttonFirst.setOnClickListener {
        //        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        //    }

        binding.buttonStratTraining.setOnClickListener {
            activity?.runOnUiThread(java.lang.Runnable {
                binding.textTimer.visibility = View.VISIBLE
                binding.progressbartimer.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                binding.textTimer.startAnimation(animation)
                binding.progressbartimer.startAnimation(animation)

                val animationount = AnimationUtils.loadAnimation(context, R.anim.fade_out)
                //    binding.CardView.startAnimation(animation)
                binding.buttonStratTraining.startAnimation(animationount)
                Handler().postDelayed({
                    //      binding.CardView.visibility = View.GONE
                    binding.buttonStratTraining.visibility = View.GONE
                }, 500)
            })
                //Handler().postDelayed({
                //    binding.loadCircle.visibility = View.GONE
                //}, 500)
            startTimer()

        }
       // Handler().postDelayed({
            //binding.loadCircle.visibility = View.GONE
       // }, 6000)
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private fun startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long = 0, action: () -> Unit) =scope.launch(Dispatchers.IO) {
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


    private fun startTimer(){

        var count = 6

        val countTimer: Job = startCoroutineTimer(0, 1050) {
            count--
            activity?.runOnUiThread(java.lang.Runnable {

                when(count){
                    5->{
                        binding.progressbartimer.setIndicatorColor(Color.parseColor("#FFE3562A"))
                        binding.progressbartimer.trackColor = Color.parseColor("#FFFFFFFF")

                    }
                    4,2,0->{

                        binding.progressbartimer.setIndicatorColor(Color.parseColor("#FF03DAC5"))
                        binding.progressbartimer.trackColor = Color.parseColor("#FFE3562A")
                        binding.progressbartimer.progress = 0

                    }
                    3,1,-1->{

                        binding.progressbartimer.setIndicatorColor(Color.parseColor("#FFE3562A"))
                        binding.progressbartimer.trackColor = Color.parseColor("#FF03DAC5")
                        binding.progressbartimer.progress = 0

                    }
                }

                binding.textTimer.text = count.toString()

                when(count){
                    5->{
                        binding.textTimer.setTextColor(Color.parseColor("#E3562A"))
                    }
                    4->{
                        binding.textTimer.setTextColor(Color.parseColor("#65AAEA"))
                    }
                    3->{
                        binding.textTimer.setTextColor(Color.parseColor("#5BA092"))
                    }
                    2->{
                        binding.textTimer.setTextColor(Color.parseColor("#F2A03F"))
                    }
                    1->{
                        binding.textTimer.setTextColor(Color.parseColor("#EF4949"))
                    }
                    else->{
                        binding.textTimer.setTextColor(Color.parseColor("#E3562A"))
                        binding.textTimer.text = "GO!"
                    }
                }

                var animator = ObjectAnimator.ofInt(binding.progressbartimer, "progress", 0, 100)
                animator.startDelay = 0
                animator.duration = 1000
                animator.interpolator = DecelerateInterpolator()
                animator.repeatCount = 0
                animator.start()
            })


        }

        viewLifecycleOwner.lifecycleScope.launch {

            delay(6300)
            countTimer.cancelAndJoin()

            val intent = Intent(requireContext(), QuestionActivity::class.java)
            startActivity(intent)

            binding.textTimer.visibility = View.VISIBLE
            binding.progressbartimer.visibility = View.VISIBLE
        }
       // countTimer.start()
        //for(i in 0..6) {

            //Thread.sleep(1000)
       // }
        //Thread.sleep(1000)
    }

    override fun onResume() {
        checkWordCount()
        super.onResume()
    }

    private fun checkWordCount(){
        viewLifecycleOwner.lifecycleScope.launch {
            var words = (activity as WordActivity).dao.getWordCount()
            var symbolsAmount = words.toString().length

            val spannable: Spannable = SpannableString("There are $words words\n in your Dictionary.")
            spannable.setSpan(ForegroundColorSpan(Color.parseColor("#E3562A")), 10, 10+symbolsAmount, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            binding.textCurwords.text = spannable

            if(symbolsAmount == 0) {
                binding.buttonStratTraining.visibility = View.GONE
                binding.textView7.visibility = View.GONE
                binding.textCurwords.text = "There are no saved words\n in your Dictionary"
            } else {
                binding.textTimer.visibility = View.GONE
                binding.progressbartimer.visibility = View.GONE
                binding.buttonStratTraining.visibility = View.VISIBLE
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}