package com.example.worldfactory

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import android.os.Bundle
import android.util.Log
import com.example.worldfactory.databinding.ActivityWordBinding

class WordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordBinding
    private lateinit var viewPager: ViewPager2
    public var currentTab : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }
}