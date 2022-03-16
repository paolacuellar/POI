package com.example.purrrfectpoi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.UsuariosModel
import com.example.purrrfectpoi.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_group.view.*


class UsuariosGruposAdapter(val usuariosGrupo: MutableList<UsuariosModel>) : RecyclerView.Adapter<UsuariosGruposAdapter.GruposViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GruposViewHolder {
       return GruposViewHolder(
        //TODO: CAMBIAR "item_group" POR "item_usuarios"
        LayoutInflater.from(parent.context).inflate(R.layout.item_group,parent,false)
       );
    }

    override fun onBindViewHolder(holder: GruposViewHolder, position: Int) {

        val usuarioGrupo : UsuariosModel = usuariosGrupo.get(position)

        holder.render(usuarioGrupo, position)

        //TODO: CAMBIAR "item_grupo_foto" DE "item_usuarioGrupo_btn_eliminar"
        holder.view.item_grupo_foto.setOnClickListener{
            //TODO: REMOVER DE LA LISTA A ESE USUARIO, ADEMAS DE ELIMINAR EL ITEM, HAY QUE ELIMINAR ESE USUARIO DEL ARREGLO "miembros" de "GrupoModel" EN BASE AL EMAIL
            //PARA ELLO, DEBEN TENER AQUÍ UN EMAIL
        }

    }

    override fun getItemCount()= usuariosGrupo.size

    class GruposViewHolder(val view: View): RecyclerView.ViewHolder(view){
        fun render(usuarioGrupo: UsuariosModel, position: Int){

            //TODO: CAMBIAR "item_grupo_nombre" Y "item_grupo_foto" POR ELEMENTOS DEL ITEM_USUARIO
            view.item_grupo_nombre.setText(if (!usuarioGrupo.Nombre.isEmpty()) usuarioGrupo.Nombre else "Error")

            FirebaseStorage.getInstance().getReference("images/Grupos/${usuarioGrupo.Foto}").downloadUrl
                .addOnSuccessListener {

                    Glide.with(view.context)
                        .load(it.toString())
                        .into(view.item_grupo_foto)
                }
        }
    }

}