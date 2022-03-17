package com.example.purrrfectpoi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.AddGroupActivity
import com.example.purrrfectpoi.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MuroFragment: Fragment() {
    
    private lateinit var buttonCrearPost : FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_muro,container,false)
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.buttonCrearPost = requireActivity().findViewById<FloatingActionButton>(R.id.menu_btn_floating)

        buttonCrearPost.setOnClickListener{
            val intent = Intent(activity, AddGroupActivity::class.java)
            startActivity(intent)
        }

    }
}