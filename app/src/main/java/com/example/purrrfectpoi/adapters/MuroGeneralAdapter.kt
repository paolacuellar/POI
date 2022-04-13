package com.example.purrrfectpoi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.PublicacionesModel
import com.example.purrrfectpoi.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_post.view.*

class MuroGeneralAdapter(val posts: MutableList<PublicacionesModel>) : RecyclerView.Adapter<MuroGeneralAdapter.MuroGeneralViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuroGeneralViewHolder {
        return MuroGeneralViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false),
            mListener
        );
    }

    override fun onBindViewHolder(holder: MuroGeneralViewHolder, position: Int) {
        val Post : PublicacionesModel = posts.get(position)

        holder.render(Post, position)
    }

    override fun getItemCount() = posts.size

    fun addItem(publicacionModel: PublicacionesModel){
        if (!this.isItemAdded(publicacionModel.id))
            posts.add(publicacionModel)
        posts.sortBy {
            it.FechaCreacion
        }
        this.notifyDataSetChanged()
    }

    fun isItemAdded(idPost : String) : Boolean{
        var itemAdded = false
        for (index in 0..posts.count() - 1){
            if (posts.get(index).id == idPost){
                itemAdded = true
                break
            }
        }

        return itemAdded
    }

    class MuroGeneralViewHolder(val view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view){
        fun render(Post: PublicacionesModel, position: Int){

            FirebaseFirestore.getInstance().collection("Usuarios").document(Post.Creador!!.id)
                .get()
                .addOnSuccessListener { responseUser ->

                    Post.FotoCreador = responseUser.get("Foto") as String
                    if (Post.FotoCreador.isNotEmpty()) {
                        FirebaseStorage.getInstance().getReference("images/Publicaciones/${Post.FotoCreador}").downloadUrl
                            .addOnSuccessListener {
                                Glide.with(view.context)
                                    .load(it.toString())
                                    .into(view.chatUserImage)
                            }
                    }
                    else {
                        view.chatUserImage!!.setImageResource(R.drawable.foto_default_perfil)
                    }

                    if (responseUser.get("Nombre") != null){
                        Post.NombreCreador = responseUser.get("Nombre") as String
                    }
                    if (responseUser.get("ApPaterno") != null){
                        Post.NombreCreador += " " + responseUser.get("ApPaterno") as String
                    }
                    view.chatNameText.setText(Post.NombreCreador)
                    view.textView.setText(Post.Texto)

                    if (Post.Foto.isNotEmpty()) {
                        FirebaseStorage.getInstance().getReference("images/Publicaciones/${Post.Foto}").downloadUrl
                            .addOnSuccessListener {
                                Glide.with(view.context)
                                    .load(it.toString())
                                    .into(view.cover)
                            }
                    }
                    else {
                        view.cover.visibility = View.GONE
                    }

                }

        }

        init {
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}