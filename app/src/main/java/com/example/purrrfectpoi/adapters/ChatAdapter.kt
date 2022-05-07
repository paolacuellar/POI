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
import kotlinx.android.synthetic.main.item_message.view.*
import kotlinx.android.synthetic.main.item_message.view.chatUserImage

class ChatAdapter(val chatMsgs: MutableList<MensajesModel>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

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
            LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false),
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

            if (Msg.Autor?.id == DataManager.emailUsuario) {

                FirebaseFirestore.getInstance().collection("Usuarios").document(Msg.Autor!!.id)
                    .get()
                    .addOnSuccessListener {

                        Msg.FotoPerfil = it.get("Foto") as String

                        if (Msg.Texto.isNotEmpty()) {

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
                            view.otherMessageContent.visibility = View.GONE
                            view.myMessageContentImage.visibility = View.GONE
                            view.otherMessageContentImage.visibility = View.GONE
                            view.myMessageContentDocument.visibility = View.GONE
                            view.otherMessageContentDocument.visibility = View.GONE
                            view.myMessageContentMap.visibility = View.GONE
                            view.otherMessageContentMap.visibility = View.GONE

                        }
                        else if (Msg.Foto.isNotEmpty()) {

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
                            view.myMessageContent.visibility = View.GONE
                            view.otherMessageContent.visibility = View.GONE
                            view.otherMessageContentImage.visibility = View.GONE
                            view.myMessageContentDocument.visibility = View.GONE
                            view.otherMessageContentDocument.visibility = View.GONE
                            view.myMessageContentMap.visibility = View.GONE
                            view.otherMessageContentMap.visibility = View.GONE

                        }
                        else if (Msg.NombreDocumento.isNotEmpty()) {

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
                            view.myMessageContent.visibility = View.GONE
                            view.otherMessageContent.visibility = View.GONE
                            view.myMessageContentImage.visibility = View.GONE
                            view.otherMessageContentImage.visibility = View.GONE
                            view.otherMessageContentDocument.visibility = View.GONE
                            view.myMessageContentMap.visibility = View.GONE
                            view.otherMessageContentMap.visibility = View.GONE

                        }
                        else if (Msg.Latitud.isNotEmpty() && Msg.Longitud.isNotEmpty()) {

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
                            view.myMessageContent.visibility = View.GONE
                            view.otherMessageContent.visibility = View.GONE
                            view.myMessageContentImage.visibility = View.GONE
                            view.otherMessageContentImage.visibility = View.GONE
                            view.myMessageContentDocument.visibility = View.GONE
                            view.otherMessageContentDocument.visibility = View.GONE
                            view.otherMessageContentMap.visibility = View.GONE

                        }

                    }

            } else {

                FirebaseFirestore.getInstance().collection("Usuarios").document(Msg.Autor!!.id)
                    .get()
                    .addOnSuccessListener {

                        Msg.FotoPerfil = it.get("Foto") as String

                        if (Msg.Texto.isNotEmpty()) {

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
                            view.myMessageContent.visibility = View.GONE
                            view.myMessageContentImage.visibility = View.GONE
                            view.otherMessageContentImage.visibility = View.GONE
                            view.myMessageContentDocument.visibility = View.GONE
                            view.otherMessageContentDocument.visibility = View.GONE
                            view.myMessageContentMap.visibility = View.GONE
                            view.otherMessageContentMap.visibility = View.GONE

                        }
                        else if (Msg.Foto.isNotEmpty()) {

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
                                view.otherMessageContentImage_otherImageView.setImageResource(R.drawable.foto_default_perfil)
                            }
                            view.myMessageContent.visibility = View.GONE
                            view.otherMessageContent.visibility = View.GONE
                            view.myMessageContentImage.visibility = View.GONE
                            view.myMessageContentDocument.visibility = View.GONE
                            view.otherMessageContentDocument.visibility = View.GONE
                            view.myMessageContentMap.visibility = View.GONE
                            view.otherMessageContentMap.visibility = View.GONE

                        }
                        else if (Msg.NombreDocumento.isNotEmpty()) {

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
                            view.myMessageContent.visibility = View.GONE
                            view.otherMessageContent.visibility = View.GONE
                            view.myMessageContentImage.visibility = View.GONE
                            view.otherMessageContentImage.visibility = View.GONE
                            view.myMessageContentDocument.visibility = View.GONE
                            view.myMessageContentMap.visibility = View.GONE
                            view.otherMessageContentMap.visibility = View.GONE

                        }
                        else if (Msg.Latitud.isNotEmpty() && Msg.Longitud.isNotEmpty()) {

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
                            view.myMessageContent.visibility = View.GONE
                            view.otherMessageContent.visibility = View.GONE
                            view.myMessageContentImage.visibility = View.GONE
                            view.otherMessageContentImage.visibility = View.GONE
                            view.myMessageContentDocument.visibility = View.GONE
                            view.otherMessageContentDocument.visibility = View.GONE
                            view.myMessageContentMap.visibility = View.GONE

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
            view.myArchive_cardView.setOnClickListener {
                listener.onDocumentClick(it, adapterPosition)
            }
            view.otherArchive_cardView.setOnClickListener {
                listener.onDocumentClick(it, adapterPosition)
            }
        }

    }

}