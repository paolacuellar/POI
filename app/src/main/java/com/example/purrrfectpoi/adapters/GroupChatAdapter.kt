package com.example.purrrfectpoi.adapters

import android.view.LayoutInflater
import android.view.VerifiedInputEvent
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

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onMapClick(view: View, position: Int)
        fun onDocumentClick(view: View, position: Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_group_message, parent, false),
            mListener
        );
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val ChatMsg : MensajesModel = chatMsgs.get(position)

        holder.render(ChatMsg, position)
    }

    override fun getItemCount()= chatMsgs.size

    fun addItem(mensajesModel: MensajesModel){
        if (!this.isItemAdded(mensajesModel.id)) {
            chatMsgs.add(mensajesModel)
            chatMsgs.sortBy {
                it.FechaCreacion
            }
            this.notifyDataSetChanged()
        }
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

    class ChatViewHolder(val view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view) {
        fun render(Msg: MensajesModel, position: Int) {

            view.myMessageContent.visibility = View.GONE
            view.otherMessageContent.visibility = View.GONE
            view.myMessageContentImage.visibility = View.GONE
            view.otherMessageContentImage.visibility = View.GONE
            view.myMessageContentDocument.visibility = View.GONE
            view.otherMessageContentDocument.visibility = View.GONE
            view.myMessageContentMap.visibility = View.GONE
            view.otherMessageContentMap.visibility = View.GONE

            if (Msg.Autor?.id == DataManager.emailUsuario) {

                FirebaseFirestore.getInstance().collection("Usuarios").document(Msg.Autor!!.id)
                    .get()
                    .addOnSuccessListener {

                        Msg.FotoPerfil = it.get("Foto") as String

                        if (Msg.Texto.isNotEmpty()) {

                            view.myMessageContent.visibility = View.VISIBLE
                            view.myMessageTextView.setText(if (!Msg.Texto.isEmpty()) Msg.Texto else "Mensaje vacío")
                            view.tv_date.setText(Msg.FechaCreacion?.toDate().toString())
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

                        }
                        else if (Msg.Foto.isNotEmpty()) {

                            view.myMessageContentImage.visibility = View.VISIBLE
                            view.myMessageContentImage_tv_date.setText(Msg.FechaCreacion?.toDate().toString())
                            FirebaseStorage.getInstance().getReference("images/Mensajes/${Msg.Foto}").downloadUrl
                                .addOnSuccessListener {
                                    Glide.with(view.context)
                                        .load(it.toString())
                                        .into(view.myMessageImage)
                                }
                            if (Msg.FotoPerfil.isNotEmpty()) {
                                FirebaseStorage.getInstance().getReference("images/Usuarios/${Msg.FotoPerfil}").downloadUrl
                                    .addOnSuccessListener {
                                        Glide.with(view.context)
                                            .load(it.toString())
                                            .into(view.myMessageContentImage_chatUserImage)
                                    }
                            }
                            else {
                                view.myMessageContentImage_chatUserImage!!.setImageResource(R.drawable.foto_default_perfil)
                            }

                        }
                        else if (Msg.Documento.isNotEmpty()) {

                            view.myMessageContentDocument.visibility = View.VISIBLE
                            view.myArchiveName.setText(Msg.NombreDocumento)
                            view.myMessageContentDocument_tv_date.setText(Msg.FechaCreacion?.toDate().toString())
                            if (Msg.FotoPerfil.isNotEmpty()) {
                                FirebaseStorage.getInstance().getReference("images/Usuarios/${Msg.FotoPerfil}").downloadUrl
                                    .addOnSuccessListener {
                                        Glide.with(view.context)
                                            .load(it.toString())
                                            .into(view.myMessageContentDocument_chatUserImage)
                                    }
                            }
                            else {
                                view.myMessageContentDocument_chatUserImage!!.setImageResource(R.drawable.foto_default_perfil)
                            }

                        }
                        else if (Msg.Latitud.isNotEmpty() && Msg.Longitud.isNotEmpty()) {

                            view.myMessageContentMap.visibility = View.VISIBLE
                            view.myMessageContentMap_tv_date.setText(Msg.FechaCreacion?.toDate().toString())
                            if (Msg.FotoPerfil.isNotEmpty()) {
                                FirebaseStorage.getInstance().getReference("images/Usuarios/${Msg.FotoPerfil}").downloadUrl
                                    .addOnSuccessListener {
                                        Glide.with(view.context)
                                            .load(it.toString())
                                            .into(view.myMessageContentMap_chatUserImage)
                                    }
                            }
                            else {
                                view.myMessageContentMap_chatUserImage!!.setImageResource(R.drawable.foto_default_perfil)
                            }

                        }

                    }

            } else {

                FirebaseFirestore.getInstance().collection("Usuarios").document(Msg.Autor!!.id)
                    .get()
                    .addOnSuccessListener {

                        var username = ""
                        if (it.get("Nombre") != null) { username = it.get("Nombre") as String }
                        if (it.get("ApPaterno") != null) { username += " " + it.get("ApPaterno") as String }
                        Msg.FotoPerfil = it.get("Foto") as String

                        if (Msg.Texto.isNotEmpty()) {

                            view.otherMessageContent.visibility = View.VISIBLE
                            view.tv_nameUserMessage.setText(username)
                            view.othersMessageTextView.setText(if (!Msg.Texto.isEmpty()) Msg.Texto else "Mensaje vacío")
                            view.tv_otherdate.setText(Msg.FechaCreacion?.toDate().toString())
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

                        }
                        else if (Msg.Foto.isNotEmpty()) {

                            view.otherMessageContentImage.visibility = View.VISIBLE
                            view.otherMessageContentImage_tv_nameUserMessage.setText(username)
                            view.otherMessageContentImage_tv_otherdate.setText(Msg.FechaCreacion?.toDate().toString())
                            FirebaseStorage.getInstance().getReference("images/Mensajes/${Msg.Foto}").downloadUrl
                                .addOnSuccessListener {
                                    Glide.with(view.context)
                                        .load(it.toString())
                                        .into(view.otherMessageImage)
                                }
                            if (Msg.FotoPerfil.isNotEmpty()) {
                                FirebaseStorage.getInstance().getReference("images/Usuarios/${Msg.FotoPerfil}").downloadUrl
                                    .addOnSuccessListener {
                                        Glide.with(view.context)
                                            .load(it.toString())
                                            .into(view.otherMessageContentImage_otherImageView)
                                    }
                            }
                            else {
                                view.otherMessageContentImage_otherImageView!!.setImageResource(R.drawable.foto_default_perfil)
                            }

                        }
                        else if (Msg.Documento.isNotEmpty()) {

                            view.otherMessageContentDocument.visibility = View.VISIBLE
                            view.otherArchiveName.setText(Msg.NombreDocumento)
                            view.otherMessageContentDocument_tv_otherdate.setText(Msg.FechaCreacion?.toDate().toString())
                            if (Msg.FotoPerfil.isNotEmpty()) {
                                FirebaseStorage.getInstance().getReference("images/Usuarios/${Msg.FotoPerfil}").downloadUrl
                                    .addOnSuccessListener {
                                        Glide.with(view.context)
                                            .load(it.toString())
                                            .into(view.otherMessageContentDocument_otherImageView)
                                    }
                            }
                            else {
                                view.otherMessageContentDocument_otherImageView!!.setImageResource(R.drawable.foto_default_perfil)
                            }

                        }
                        else if (Msg.Latitud.isNotEmpty() && Msg.Longitud.isNotEmpty()) {

                            view.otherMessageContentMap.visibility = View.VISIBLE
                            view.otherMessageContentMap_tv_nameUserMessage.setText(username)
                            view.otherMessageContentMap_tv_otherdate.setText(Msg.FechaCreacion?.toDate().toString())
                            if (Msg.FotoPerfil.isNotEmpty()) {
                                FirebaseStorage.getInstance().getReference("images/Usuarios/${Msg.FotoPerfil}").downloadUrl
                                    .addOnSuccessListener {
                                        Glide.with(view.context)
                                            .load(it.toString())
                                            .into(view.otherMessageContentMap_otherImageView)
                                    }
                            }
                            else {
                                view.otherMessageContentMap_otherImageView!!.setImageResource(R.drawable.foto_default_perfil)
                            }

                        }

                    }

            }

        }

        init {
            view.myMap.setOnClickListener {
                listener.onMapClick(it, adapterPosition)
            }
            view.otherMap.setOnClickListener {
                listener.onMapClick(it, adapterPosition)
            }
            view.myArchive.setOnClickListener {
                listener.onDocumentClick(it, adapterPosition)
            }
            view.otherArchive.setOnClickListener {
                listener.onDocumentClick(it, adapterPosition)
            }
        }

    }

}