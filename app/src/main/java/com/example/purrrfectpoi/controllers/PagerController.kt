package com.example.purrrfectpoi.controllers

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.purrrfectpoi.adapters.GroupTasksAdapter
import com.example.purrrfectpoi.fragments.GroupChatFragment
import com.example.purrrfectpoi.fragments.GroupMembersFragment
import com.example.purrrfectpoi.fragments.GroupTasksFragment

class PagerController(private val myContext: Context,fm:FragmentManager,internal var totalTabs:Int):FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                return GroupMembersFragment()
            }
            1->{
                return GroupChatFragment()
            }
            2->{
                return GroupTasksFragment()
            }
            else -> GroupMembersFragment()
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}