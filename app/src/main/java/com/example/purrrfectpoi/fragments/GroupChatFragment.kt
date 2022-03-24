package com.example.purrrfectpoi.fragments

import android.content.ContentValues
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.Models.MensajesModel
import com.example.purrrfectpoi.R
import com.example.purrrfectpoi.adapters.ChatAdapter
import com.example.purrrfectpoi.adapters.GroupChatAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.psm.hiring.Utils.DataManager

class GroupChatFragment:Fragment() {

    private lateinit var recyclerViewMsgs : RecyclerView
    private lateinit var groupChatAdapter : GroupChatAdapter

    var btnEnviarMsg : ImageView? = null;
    var txtMsg : EditText? = null;

    var idGrupo : String? = null;
    //var idChatGrupo : String? = null;

    private lateinit var db : FirebaseFirestore
    private lateinit var documentReferenceUserLogged : DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_group_chat,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.btnEnviarMsg = getView()?.findViewById<ImageView>(R.id.sendMessageButton)
        this.txtMsg = getView()?.findViewById<EditText>(R.id.messageTextField)

        this.recyclerViewMsgs = view.findViewById<RecyclerView>(R.id.messagesRecyclerView)

        this.btnEnviarMsg?.setOnClickListener {
            enviarMensaje()
        }

        documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(
            DataManager.emailUsuario!!)

        idGrupo = arguments?.getString("groupId")
        //myPhoto = DataManager.fotoUsuario

        setUpChat()
        updateChat()

    }

    private fun updateChat() {
        db = FirebaseFirestore.getInstance()
        db.collection("Grupos").document(idGrupo!!)
            .get()
            .addOnSuccessListener { responseGrupo ->

                if (responseGrupo.get("Conversacion") != null) {

                    var idChatGrupo = responseGrupo.get("Conversacion") as DocumentReference
                    db.collection("Conversacion").document(idChatGrupo.id).collection("Mensajes")
                        .addSnapshotListener { snapshot, e ->

                            if  (e != null) {
                                return@addSnapshotListener
                            }

                            if (snapshot != null) {
                                db.collection("Conversacion").document(idChatGrupo.id).collection("Mensajes")
                                    .orderBy("FechaCreacion", Query.Direction.DESCENDING).limit(1)
                                    .get()
                                    .addOnSuccessListener { responseMsgs ->

                                        for (responseMsg in responseMsgs) {

                                            if (responseMsg.get("FechaCreacion") != null) {

                                                var msgAux = MensajesModel()
                                                msgAux.id = responseMsg.id
                                                msgAux.Texto = if(responseMsg.get("Texto") != null)    responseMsg.get("Texto") as String else ""
                                                msgAux.Autor = responseMsg.get("Autor") as DocumentReference
                                                msgAux.Foto = if(responseMsg.get("Foto") != null)    responseMsg.get("Foto") as String else ""
                                                msgAux.NombreDocumento = if(responseMsg.get("NombreDocumento") != null)    responseMsg.get("NombreDocumento") as String else ""
                                                msgAux.Latitud = if(responseMsg.get("Latitud") != null)    responseMsg.get("Latitud") as String else ""
                                                msgAux.Longitud = if(responseMsg.get("Longitud") != null)    responseMsg.get("Longitud") as String else ""
                                                msgAux.FechaCreacion = responseMsg.get("FechaCreacion") as Timestamp

                                                groupChatAdapter.addItem(msgAux)

                                            }

                                        }

                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w(ContentValues.TAG, "Error consiguiendo el último Mensaje", exception)
                                    }

                            }

                        }

                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error consiguiendo el Grupo", exception)
            }

    }

    private fun setUpChat() {
        var msgParam : MutableList<MensajesModel> = mutableListOf()
        groupChatAdapter = GroupChatAdapter(msgParam)
        recyclerViewMsgs.apply {
            adapter = groupChatAdapter
            layoutManager = LinearLayoutManager(context)
        }

        db = FirebaseFirestore.getInstance()
        db.collection("Grupos").document(idGrupo!!)
            .get()
            .addOnSuccessListener { responseGrupo ->

                if (responseGrupo.get("Conversacion") != null) {

                    var idChatGrupo = responseGrupo.get("Conversacion") as DocumentReference
                    db.collection("Conversacion").document(idChatGrupo.id).collection("Mensajes")
                        .get()
                        .addOnSuccessListener { responseMsgs ->

                            for (responseMsg in responseMsgs) {

                                var msgAux = MensajesModel()
                                msgAux.id = responseMsg.id
                                msgAux.Texto = if(responseMsg.get("Texto") != null)    responseMsg.get("Texto") as String else ""
                                msgAux.Autor = responseMsg.get("Autor") as DocumentReference
                                msgAux.Foto = if(responseMsg.get("Foto") != null)    responseMsg.get("Foto") as String else ""
                                msgAux.NombreDocumento = if(responseMsg.get("NombreDocumento") != null)    responseMsg.get("NombreDocumento") as String else ""
                                msgAux.Latitud = if(responseMsg.get("Latitud") != null)    responseMsg.get("Latitud") as String else ""
                                msgAux.Longitud = if(responseMsg.get("Longitud") != null)    responseMsg.get("Longitud") as String else ""
                                msgAux.FechaCreacion = responseMsg.get("FechaCreacion") as Timestamp

                                msgParam.add(msgAux)
                                groupChatAdapter.addItem(msgAux)

                            }

                        }
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error consiguiendo los Mensajes", exception)
                        }
                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error consiguiendo el Grupo", exception)
            }

    }

    private fun enviarMensaje() {
        var mensaje : String = txtMsg?.text.toString()
        if (mensaje.isEmpty()) {
            txtMsg?.setError("Mensaje vacío")

        } else {
            txtMsg?.setText("")

            db = FirebaseFirestore.getInstance()
            db.collection("Grupos").document(idGrupo!!)
                .get()
                .addOnSuccessListener { responseGrupo ->

                    if (responseGrupo.get("Conversacion") != null) {

                        var idChatGrupo = responseGrupo.get("Conversacion") as DocumentReference
                        db.collection("Conversacion").document(idChatGrupo.id).collection("Mensajes")
                            .add(
                                hashMapOf(
                                    "Texto" to mensaje,
                                    "Autor" to documentReferenceUserLogged,
                                    "FechaCreacion" to FieldValue.serverTimestamp()
                                )
                            )
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error consiguiendo el Grupo", exception)
                }

        }

    }

}