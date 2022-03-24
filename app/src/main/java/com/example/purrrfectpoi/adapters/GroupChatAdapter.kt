package com.example.purrrfectpoi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrrfectpoi.Models.MensajesModel
import com.example.purrrfectpoi.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import kotlinx.android.synthetic.main.item_group_message.view.*

class GroupChatAdapter(val chatMsgs: MutableList<MensajesModel>) : RecyclerView.Adapter<GroupChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_group_message, parent, false)
        );
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val ChatMsg : MensajesModel = chatMsgs.get(position)

        holder.render(ChatMsg, position)
    }

    override fun getItemCount()= chatMsgs.size

    fun addItem(mensajesModel: MensajesModel){
        if (!this.isItemAdded(mensajesModel.id))
            chatMsgs.add(mensajesModel)
        chatMsgs.sortBy {
            it.FechaCreacion
        }
        this.notifyDataSetChanged()
    }

    fun isItemAdded(idMsg : String) : Boolean{
        var itemAdded = false
        for (index in 0..chatMsgs.count() - 1) {
            if (chatMsgs.get(index).id == idMsg) {
                itemAdded = true
                break
            }
        }

        return itemAdded
    }

    class ChatViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun render(Msg: MensajesModel, position: Int) {

            if (Msg.Autor?.id == DataManager.emailUsuario) {

                FirebaseFirestore.getInstance().collection("Usuarios").document(Msg.Autor!!.id)
                    .get()
                    .addOnSuccessListener {

                        Msg.FotoPerfil = it.get("Foto") as String

                        view.myMessageTextView.setText(if (!Msg.Texto.isEmpty()) Msg.Texto else "Mensaje vacío")
                        view.tv_date.setText((Msg.FechaCreacion?.toDate()).toString())
                        if (Msg.FotoPerfil.isNotEmpty()) {
                            FirebaseStorage.getInstance().getReference("images/Usuarios/${Msg.FotoPerfil}").downloadUrl
                                .addOnSuccessListener {
                                    Glide.with(view.context)
                                        .load(it.toString())
                                        .into(view.chatUserImage)
                                }
                        }
                        else {
                            view.chatUserImage!!.setImageResource(R.drawable.foto_default_perfil)
                        }
                        view.otherMessageContent.visibility = View.GONE

                    }

            } else {

                FirebaseFirestore.getInstance().collection("Usuarios").document(Msg.Autor!!.id)
                    .get()
                    .addOnSuccessListener {
                        var username = ""

                        if (it.get("Nombre") != null) {
                            username = it.get("Nombre") as String
                        }
                        if (it.get("ApPaterno") != null) {
                            username += " " + it.get("ApPaterno") as String
                        }
                        view.tv_nameUserMessage.setText(username)

                        Msg.FotoPerfil = it.get("Foto") as String

                        view.othersMessageTextView.setText(if (!Msg.Texto.isEmpty()) Msg.Texto else "Mensaje vacío")
                        view.tv_otherdate.setText((Msg.FechaCreacion?.toDate()).toString())
                        if (Msg.FotoPerfil.isNotEmpty()) {
                            FirebaseStorage.getInstance().getReference("images/Usuarios/${Msg.FotoPerfil}").downloadUrl
                                .addOnSuccessListener {
                                    Glide.with(view.context)
                                        .load(it.toString())
                                        .into(view.otherImageView)
                                }
                        }
                        else {
                            view.otherImageView!!.setImageResource(R.drawable.foto_default_perfil)
                        }
                        view.myMessageContent.visibility = View.GONE

                    }

            }

        }
    }

}