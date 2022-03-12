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
import com.example.purrrfectpoi.Models.GruposModel


import com.example.purrrfectpoi.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_group.view.*
import kotlinx.android.synthetic.main.item_reward.view.item_medalla_nombre
import java.io.File
import java.nio.file.Paths


class GruposAdapter(val grupos: MutableList<GruposModel>) : RecyclerView.Adapter<GruposAdapter.GruposViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GruposViewHolder {
       return GruposViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_group,parent,false)
       );
    }

    override fun onBindViewHolder(holder: GruposViewHolder, position: Int) {

        val Grupos : GruposModel = grupos.get(position)

         holder.render(Grupos, position)

        holder.view.setOnClickListener{

        }

    }

    override fun getItemCount()= grupos.size

    class GruposViewHolder(val view: View): RecyclerView.ViewHolder(view){
        fun render(Grupos: GruposModel, position: Int){

            view.item_grupo_nombre.setText(if (!Grupos.Nombre.isEmpty()) Grupos.Nombre else "Error")

            FirebaseStorage.getInstance().getReference("images/Grupos/${Grupos.Foto}").downloadUrl
                .addOnSuccessListener {

                    Glide.with(view.context)
                        .load(it.toString())
                        .into(view.item_grupo_foto)
                }

        }
    }

}