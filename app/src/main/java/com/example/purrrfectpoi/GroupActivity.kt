package com.example.purrrfectpoi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.purrrfectpoi.fragments.GroupChatFragment
import com.example.purrrfectpoi.fragments.GroupMembersFragment
import com.example.purrrfectpoi.fragments.GroupTasksFragment

class GroupActivity : AppCompatActivity() {

    val TAG="GroupActivity"

    var btnRegresar : ImageView? =  null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val bundle : Bundle?= intent.extras

        val bundleGroupInfo = Bundle().apply {
            putString("grupoId", bundle!!.getString("grupoId"))
        }
        val chatFragment= GroupChatFragment()
        chatFragment.arguments = bundleGroupInfo

        val miembrosFragment= GroupMembersFragment()
        miembrosFragment.arguments = bundleGroupInfo

        val tareasFragment= GroupTasksFragment()
        tareasFragment.arguments = bundleGroupInfo

        this.btnRegresar = findViewById<ImageView>(R.id.btn_grupo_regresar)
        this.btnRegresar?.setOnClickListener {
            onBackPressed()
        }

        setCurrentFragment(chatFragment)

        val bottom_navigation=findViewById<BottomNavigationView>(R.id.bottom_navigation_group)

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
                R.id.nav_tasks ->{
                    setCurrentFragment(tareasFragment)
                    Log.i(TAG, "Tareas Selected")
                }
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