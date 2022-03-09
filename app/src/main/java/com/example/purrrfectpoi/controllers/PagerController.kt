package com.example.purrrfectpoi.controllers

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.purrrfectpoi.fragments.GrupoChatFragment
import com.example.purrrfectpoi.fragments.GrupoMiembrosFragment
import com.example.purrrfectpoi.fragments.GrupoTareasFragment

class PagerController(private val myContext: Context,fm:FragmentManager,internal var totalTabs:Int):FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        var fragmentReturned =  when (position) {
            0 -> {
                return GrupoMiembrosFragment()
            }
            1 -> {
                return GrupoChatFragment()
            }
            2 -> {
                return GrupoTareasFragment()
            }
            else -> GrupoTareasFragment()
            // TODO: ESTO NO DEBE IR AQUÍ, CÓMO SOLUCIONAR?

        }

        return fragmentReturned
    }

    override fun getCount(): Int {
        return totalTabs
    }
}