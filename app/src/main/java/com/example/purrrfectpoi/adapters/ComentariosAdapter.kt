package com.example.purrrfectpoi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.ComentariosModel
import com.example.purrrfectpoi.Models.UsuariosModel
import com.example.purrrfectpoi.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import kotlinx.android.synthetic.main.item_comment.view.*

class ComentariosAdapter(val coms: MutableList<ComentariosModel>) : RecyclerView.Adapter<ComentariosAdapter.ComentariosViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentariosViewHolder {
        return ComentariosViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false),
            mListener
        );
    }

    override fun onBindViewHolder(holder: ComentariosViewHolder, position: Int) {
        val Com : ComentariosModel = coms.get(position)

        holder.render(Com, position)

    }

    override fun getItemCount()= coms.size

    fun addItem(comentariosModel: ComentariosModel){
        if (!this.isItemAdded(comentariosModel.id))
            coms.add(comentariosModel)
        coms.sortBy {
            it.FechaCreacion
        }
        this.notifyDataSetChanged()
    }

    fun isItemAdded(idCom : String) : Boolean{
        var itemAdded = false
        for (index in 0..coms.count() - 1) {
            if (coms.get(index).id == idCom) {
                itemAdded = true
                break
            }
        }

        return itemAdded
    }

    fun deleteItem(idCom: String) {
        for (index in 0..coms.count() - 1) {
            if (coms.get(index).id == idCom) {
                coms.removeAt(index)
                break
            }
        }
        this.notifyDataSetChanged()
    }

    class ComentariosViewHolder(val view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view) {
        fun render(Com: ComentariosModel, position: Int) {
            FirebaseFirestore.getInstance().collection("Usuarios").document(Com.Creador!!.id)
                .get()
                .addOnSuccessListener { responseUsuario ->

                    view.commentText.setText(if (!Com.Texto.isEmpty()) Com.Texto else "Comentario vacío")

                    var userAux = UsuariosModel()
                    userAux.Nombre = if(responseUsuario.get("Nombre") != null)    responseUsuario.get("Nombre") as String else ""
                    userAux.ApPaterno =  if(responseUsuario.get("ApPaterno") != null) responseUsuario.get("ApPaterno") as String else ""
                    userAux.Ecriptado = if(responseUsuario.get("Ecriptado") != null) responseUsuario.get("Ecriptado") as Boolean else false
                    userAux.DesencriptarInfo()

                    var username = userAux.Nombre + " " + userAux.ApPaterno
                    view.chatNameText.setText(username)

                    var userPhoto = responseUsuario.get("Foto") as String
                    if (userPhoto.isNotEmpty()) {
                        FirebaseStorage.getInstance().getReference("images/Usuarios/${userPhoto}").downloadUrl
                            .addOnSuccessListener {
                                Glide.with(view.context)
                                    .load(it.toString())
                                    .into(view.chatUserImage)
                            }
                    }
                    else {
                        view.chatUserImage!!.setImageResource(R.drawable.foto_default_perfil)
                    }

                    if (Com.Creador?.id != DataManager.emailUsuario) {
                        view.deleteCom.visibility = View.GONE
                    }

                }
        }

        init {
            view.deleteCom.setOnClickListener {
                listener.onItemClick(it, adapterPosition)
            }
        }
    }

}