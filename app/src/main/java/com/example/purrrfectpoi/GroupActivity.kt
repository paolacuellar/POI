package com.example.purrrfectpoi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class GroupActivity : AppCompatActivity() {

    var tabLayout:TabLayout?=null
    var viewPager:ViewPager?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        tabLayout=findViewById<TabLayout>(R.id.tablayout)
        viewPager=findViewById<ViewPager>(R.id.viewpager)

    }
}