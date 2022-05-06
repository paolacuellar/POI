package com.example.purrrfectpoi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.UsuariosModel
import com.example.purrrfectpoi.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_chat.view.*

class UsuariosChatsAdapter(val userChats: MutableList<UsuariosModel>, val buttonVisible: Boolean = true) : RecyclerView.Adapter<UsuariosChatsAdapter.UsuariosChatsViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuariosChatsViewHolder {
        return UsuariosChatsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false),
            mListener
        );
    }

    override fun onBindViewHolder(holder: UsuariosChatsViewHolder, position: Int) {
        val UserChat : UsuariosModel = userChats.get(position)

        holder.render(UserChat, position)

        if(buttonVisible){
            holder.view.newChatButton.visibility = View.VISIBLE
        }
        else{
            holder.view.newChatButton.visibility = View.GONE
        }

    }

    override fun getItemCount()= userChats.size

    fun addItem(usuarioModel : UsuariosModel){
        if (!this.isItemAdded(usuarioModel.Email))
            userChats.add(usuarioModel)
        this.notifyDataSetChanged()
    }

    fun isItemAdded(emailUser : String) : Boolean{
        var itemAdded = false
        for (index in 0..userChats.count() - 1) {
            if (userChats.get(index).Email == emailUser) {
                itemAdded = true
                break
            }
        }

        return itemAdded
    }

    class UsuariosChatsViewHolder(val view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view){
        fun render(UserChat: UsuariosModel, position: Int){

            view.statusTextView.setText(if (UserChat.Conectado) "Conectado" else "Desconectado")
            view.chatNameText.setText(if (!UserChat.Nombre.isEmpty()) UserChat.Nombre + " " + UserChat.ApPaterno else "Error")
            if (UserChat.Foto.isNotEmpty()) {
                FirebaseStorage.getInstance().getReference("images/Usuarios/${UserChat.Foto}").downloadUrl
                    .addOnSuccessListener {
                        Glide.with(view.context)
                            .load(it.toString())
                            .into(view.chatUserImage)
                    }
            }
            else {
                view.chatUserImage!!.setImageResource(R.drawable.foto_default_perfil)
            }
        }

        init {
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }

}