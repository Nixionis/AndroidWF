package com.example.worldfactory

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.worldfactory.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    private lateinit var viewPager: ViewPager2

    private var currentPageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewpagerIntro
        viewPager.isUserInputEnabled = false
        viewPager.adapter = PagerAdapterIntro(this)

        binding.buttonNext.setOnClickListener {

            if(currentPageIndex < 2){
                currentPageIndex++
                viewPager.setCurrentItem(currentPageIndex, true)

                if(currentPageIndex == 2) binding.buttonNext.text = "Let's Start"
            } else {
                //Let's get started!
            }
        }
    }

    override fun onBackPressed() {
        if(currentPageIndex > 0){
            currentPageIndex--
            viewPager.setCurrentItem(currentPageIndex, true)
            if(currentPageIndex != 2) binding.buttonNext.text = "Next"
        } else {
            super.onBackPressed()
        }
    }
}