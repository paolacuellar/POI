package com.example.purrrfectpoi.adapters

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.UsuariosModel


import com.example.purrrfectpoi.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_add_group_members.view.*


class UsuariosGruposAdapter(val listaUsuariosGrupo: MutableList<UsuariosModel>) : RecyclerView.Adapter<UsuariosGruposAdapter.GruposViewHolder>(){

    fun addItem(usuarioModel : UsuariosModel){
        if (!this.isItemAdded(usuarioModel.Email))
            listaUsuariosGrupo.add(usuarioModel)
        this.notifyDataSetChanged()
    }

    private fun deleteItemByEmail(emailUser : String){
        var itemPosition : Int? = null

        for (index in 0..listaUsuariosGrupo.count() - 1) {
            if (listaUsuariosGrupo.get(index).Email == emailUser) {
                itemPosition = index
                break
            }
        }

        if (itemPosition != null){
            listaUsuariosGrupo.removeAt(itemPosition)
            this.notifyDataSetChanged()
        }

    }

    public fun isItemAdded(emailUser : String) : Boolean{
        var itemAdded = false

        for (index in 0..listaUsuariosGrupo.count() - 1) {
            if (listaUsuariosGrupo.get(index).Email == emailUser) {
                itemAdded = true
                break
            }
        }

        return itemAdded
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GruposViewHolder {
       return GruposViewHolder(
        //TODO: CAMBIAR "item_group" POR "item_usuarios"
        LayoutInflater.from(parent.context).inflate(R.layout.item_add_group_members,parent,false)
       );
    }

    override fun onBindViewHolder(holder: GruposViewHolder, position: Int) {

        val usuarioGrupo : UsuariosModel = listaUsuariosGrupo.get(position)

        holder.render(usuarioGrupo, position)

        holder.view.item_add_grupo_btn_eliminar.setOnClickListener{
            Log.w(TAG, "SE ELIMINO AL USUARIO ${usuarioGrupo.Email}")

            deleteItemByEmail(usuarioGrupo.Email)
        }

        /*
        //TODO: CAMBIAR "item_grupo_foto" DE "item_usuarioGrupo_btn_eliminar"
        holder.view.item_grupo_foto.setOnClickListener{
            //TODO: REMOVER DE LA LISTA A ESE USUARIO, ADEMAS DE ELIMINAR EL ITEM, HAY QUE ELIMINAR ESE USUARIO DEL ARREGLO "miembros" de "GrupoModel" EN BASE AL EMAIL
            //PARA ELLO, DEBEN TENER AQUÍ UN EMAIL
        }
        */

    }

    override fun getItemCount() = listaUsuariosGrupo.size

    class GruposViewHolder(val view: View): RecyclerView.ViewHolder(view){

        fun render(usuarioGrupo: UsuariosModel, position: Int){

            //TODO: CAMBIAR "item_grupo_nombre" Y "item_grupo_foto" POR ELEMENTOS DEL ITEM_USUARIO
            view.item_add_grupo_nombre.text = if (!usuarioGrupo.Nombre.isEmpty()) usuarioGrupo.Nombre else "Error"

            view.item_add_grupo_email.text = if (!usuarioGrupo.Email.isEmpty()) usuarioGrupo.Email else "Error"

            if (usuarioGrupo.Foto.isNotEmpty()) {

                FirebaseStorage.getInstance().getReference("images/Usuarios/${usuarioGrupo.Foto}").downloadUrl
                    .addOnSuccessListener {

                        Glide.with(view.context)
                            .load(it.toString())
                            .into(view.item_add_grupo_foto)
                    }
            }
            else {
                view.item_add_grupo_foto!!.setImageResource(R.drawable.foto_default_perfil)
            }


            view.item_add_grupo_nombre.text = if (!usuarioGrupo.Nombre.isEmpty()) usuarioGrupo.Nombre else "Error"

        }
    }

}