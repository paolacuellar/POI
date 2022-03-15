package com.example.purrrfectpoi.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.GruposModel


import com.example.purrrfectpoi.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_group.view.*

import com.example.purrrfectpoi.GroupActivity


class GruposAdapter(val grupos: MutableList<GruposModel>) : RecyclerView.Adapter<GruposAdapter.GruposViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GruposViewHolder {
       return GruposViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_group,parent,false)
       );
    }

    override fun onBindViewHolder(holder: GruposViewHolder, position: Int) {
        val Grupo : GruposModel = grupos.get(position)

        holder.render(Grupo, position)


    }

    override fun getItemCount()= grupos.size

    class GruposViewHolder(val view: View): RecyclerView.ViewHolder(view){
        fun render(Grupo: GruposModel, position: Int){

            view.item_grupo_nombre.setText(if (!Grupo.Nombre.isEmpty()) Grupo.Nombre else "Error")

            FirebaseStorage.getInstance().getReference("images/Grupos/${Grupo.Foto}").downloadUrl
                .addOnSuccessListener {

                    Glide.with(view.context)
                        .load(it.toString())
                        .into(view.item_grupo_foto)
                }


            view.item_grupo_card.setOnClickListener{
                val intent = Intent(view.context, GroupActivity::class.java)
                intent.putExtra("groupId",Grupo.id)
                view.context?.startActivity(intent)
            }
        }
    }

}