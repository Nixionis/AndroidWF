package com.example.worldfactory

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapterIntro(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {FirstFragmentIntro()}
            1 -> {SecondFragmentIntro()}
            2 -> {ThirdFragmentIntro()}
            else -> {throw Resources.NotFoundException("Position for intro not found!")}
        }
    }
}