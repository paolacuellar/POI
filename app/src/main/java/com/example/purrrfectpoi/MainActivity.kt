package com.example.purrrfectpoi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.purrrfectpoi.fragments.ChatsFragment
import com.example.purrrfectpoi.fragments.GruposFragment
import com.example.purrrfectpoi.fragments.MuroFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    val TAG="MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val muroFragment=MuroFragment()
        val chatsFragment=ChatsFragment()
        val gruposFragment=GruposFragment()

        setCurrentFragment(muroFragment)

        val bottom_navigation=findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottom_navigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_chats ->{
                    setCurrentFragment(chatsFragment)
                    Log.i(TAG, "Chats Selected")
                }
                R.id.nav_muro ->{
                    setCurrentFragment(muroFragment)
                    Log.i(TAG, "Muro Selected")
                }
                R.id.nav_grupos ->{
                    setCurrentFragment(gruposFragment)
                    Log.i(TAG, "Grupos Selected")
                }
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.content,fragment)
            commit()
        }
}


