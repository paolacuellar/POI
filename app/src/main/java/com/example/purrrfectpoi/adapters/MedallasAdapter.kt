package com.example.purrrfectpoi.adapters

import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.MedallasModel
import kotlinx.android.synthetic.main.item_reward.view.*


import com.example.purrrfectpoi.R
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.nio.file.Paths


class MedallasAdapter(val rewards: MutableList<MedallasModel>) : RecyclerView.Adapter<MedallasAdapter.MedallasViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedallasViewHolder {
       return MedallasViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_reward,parent,false)
       );
    }

    override fun onBindViewHolder(holder: MedallasViewHolder, position: Int) {

        val Medallas : MedallasModel = rewards.get(position)

         holder.render(Medallas, position)

        holder.view.setOnClickListener{

        }

    }

    override fun getItemCount()= rewards.size

    class MedallasViewHolder(val view: View): RecyclerView.ViewHolder(view){
        fun render(Medallas: MedallasModel, position: Int){

            view.item_medalla_nombre.setText(if (!Medallas.Nombre.isEmpty()) Medallas.Nombre else "Error")

            FirebaseStorage.getInstance().getReference("images/Medallas/${Medallas.Foto}").downloadUrl
                .addOnSuccessListener {

                    var message = it.toString()

                    Glide.with(view.context)
                        .load(it.toString())
                        .into(view.item_medalla_foto)
                }

        }
    }

}