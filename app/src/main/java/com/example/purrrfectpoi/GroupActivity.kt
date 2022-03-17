package com.example.purrrfectpoi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.purrrfectpoi.fragments.GroupChatFragment
import com.example.purrrfectpoi.fragments.GroupMembersFragment

class GroupActivity : AppCompatActivity() {

    val TAG="GroupActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val chatFragment= GroupChatFragment()
        val miembrosFragment= GroupMembersFragment()

        setCurrentFragment(chatFragment)

        val bottom_navigation=findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottom_navigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_chat ->{
                    setCurrentFragment(chatFragment)
                    Log.i(TAG, "Chat Selected")
                }
                R.id.nav_members ->{
                    setCurrentFragment(miembrosFragment)
                    Log.i(TAG, "Members Selected")
                }
                /*R.id.nav_grupos ->{
                    setCurrentFragment(gruposFragment)
                    Log.i(TAG, "Grupos Selected")
                }*/
            }
            true
        }

        /*val groupId = intent.getStringExtra("groupId")

        DataManager.showToast(this, "El id del grupo es ${groupId}")*/
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.content_group,fragment)
            commit()
        }
}