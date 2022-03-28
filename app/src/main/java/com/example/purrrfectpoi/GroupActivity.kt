package com.example.purrrfectpoi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.purrrfectpoi.fragments.GroupChatFragment
import com.example.purrrfectpoi.fragments.GroupMembersFragment

class GroupActivity : AppCompatActivity() {

    val TAG="GroupActivity"

    var btnRegresar : ImageView? =  null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val bundle : Bundle?= intent.extras

        val bundleChat = Bundle().apply {
            putString("grupoId", bundle!!.getString("grupoId"))
        }
        val chatFragment= GroupChatFragment()
        chatFragment.arguments = bundleChat

        val miembrosFragment= GroupMembersFragment()

        this.btnRegresar = findViewById<ImageView>(R.id.btn_grupo_regresar)
        this.btnRegresar?.setOnClickListener {
            onBackPressed()
        }

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
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.content_group,fragment)
            commit()
        }
}