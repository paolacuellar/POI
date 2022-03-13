package com.example.purrrfectpoi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.MedallasModel
import kotlinx.android.synthetic.main.item_reward.view.*


import com.example.purrrfectpoi.R
import com.google.firebase.storage.FirebaseStorage
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.core.graphics.drawable.toBitmap


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
        fun render(Medalla: MedallasModel, position: Int){

            //TODO: REVISAR POR QUE FALLAN LOS VALORES AL SCROLLEAR RAPIDAMENTE
            view.item_medalla_nombre.setText(if (!Medalla.Nombre.isEmpty()) Medalla.Nombre else "Error")

            FirebaseStorage.getInstance().getReference("images/Medallas/${Medalla.Foto}").downloadUrl
                .addOnSuccessListener {

                    var message = it.toString()

                    Glide.with(view.context)
                        .load(it.toString())
                        .into(view.item_medalla_foto)

                    if (!Medalla.MedallaObtenida){
                        setImageDark()
                    }
                }

        }

        private fun setImageDark(){
            val colorMatrix = ColorMatrix()

            colorMatrix.setSaturation(0f)

            val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)

            view.item_medalla_foto.colorFilter = colorMatrixColorFilter
        }
    }

}