package com.example.purrrfectpoi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.AddGroupActivity
import com.example.purrrfectpoi.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MuroFragment: Fragment() {

    var txtTituloPantalla : TextView? = null;
    private lateinit var buttonCrearPost : FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_muro,container,false)
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.txtTituloPantalla = requireActivity().findViewById<TextView>(R.id.main_text_title)
        this.txtTituloPantalla!!.text = "Muro General";

        this.buttonCrearPost = requireActivity().findViewById<FloatingActionButton>(R.id.menu_btn_floating)
        this.buttonCrearPost.visibility = View.VISIBLE
        //this.buttonCrearPost.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_write_brown));
        //this.buttonCrearPost.setBackgroundResource(R.drawable.ic_write_brown);
        this.buttonCrearPost.setImageResource(R.drawable.ic_write_brown);

        buttonCrearPost.setOnClickListener{
            val intent = Intent(activity, AddGroupActivity::class.java)
            startActivity(intent)
        }

    }
}