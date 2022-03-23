package com.example.purrrfectpoi

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.Models.ConversacionesModel
import com.example.purrrfectpoi.Models.MensajesModel
import com.example.purrrfectpoi.adapters.ChatAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firestore.v1.DocumentTransform
import com.psm.hiring.Utils.DataManager
import java.util.*
import kotlin.Comparator

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerViewMsgs : RecyclerView
    private lateinit var chatAdapter : ChatAdapter

    var btnRegresar : ImageView? =  null;
    var txtUsername : TextView? = null;
    var btnEnviarMsg : ImageView? = null;
    var txtMsg : EditText? = null;

    var idChat : String? = null;
    var chatTo : String? = null;
    var chatFrom : String? = null;

    var otherPhoto : String? = null;
    var myPhoto : String? = null;

    private lateinit var db : FirebaseFirestore
    private lateinit var documentReferenceUserLogged : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        this.recyclerViewMsgs = findViewById<RecyclerView>(R.id.messagesRecyclerView)

        val bundle : Bundle?= intent.extras
        idChat = bundle!!.getString("IdChat")
        chatTo = bundle!!.getString("Email")
        chatFrom = DataManager.emailUsuario

        myPhoto = DataManager.fotoUsuario

        this.btnRegresar = findViewById<ImageView>(R.id.chat_btn_regresar)
        this.txtUsername = findViewById<TextView>(R.id.chat_txt_nombre_user)
        this.btnEnviarMsg = findViewById<ImageView>(R.id.sendMessageButton)
        this.txtMsg = findViewById<EditText>(R.id.messageTextField)

        this.btnRegresar?.setOnClickListener {
            val vIntent = Intent(this, MainActivity::class.java)
            startActivity(vIntent)
        }
        txtUsername?.text = chatTo
        this.btnEnviarMsg?.setOnClickListener {
            enviarMensaje()
        }

        documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(
            DataManager.emailUsuario!!)

        setUpChat()
        updateChat()

    }

    private fun updateChat() {
        db = FirebaseFirestore.getInstance()
        db.collection("Conversacion").document(idChat!!).collection("Mensajes")
            .addSnapshotListener { snapshot, e ->

                if  (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    db.collection("Conversacion").document(idChat!!).collection("Mensajes")
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
                                    if (msgAux.Autor == documentReferenceUserLogged) {
                                        msgAux.FotoPerfil = if(myPhoto != null)    myPhoto as String else ""
                                    } else {
                                        msgAux.FotoPerfil = if(otherPhoto != null)    otherPhoto as String else ""
                                    }

                                    chatAdapter.addItem(msgAux)

                                }

                            }

                        }
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error consiguiendo el último Mensaje", exception)
                        }

                }

            }

    }

    private fun setUpChat() {
        db = FirebaseFirestore.getInstance()
        db.collection("Usuarios").document(documentReferenceUserLogged.id).get()
            .addOnSuccessListener { responseUsuario ->
                myPhoto = if(responseUsuario.get("Foto") != null)      responseUsuario.get("Foto") as String else ""
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error consiguiendo el Usuario", exception)
            }

        db.collection("Usuarios").document(chatTo!!).get()
            .addOnSuccessListener { responseUsuario ->

                var msgParam : MutableList<MensajesModel> = mutableListOf()
                chatAdapter = ChatAdapter(msgParam)
                recyclerViewMsgs.apply {
                    adapter = chatAdapter
                    layoutManager = LinearLayoutManager(context)
                }

                var username : String = ""
                if(responseUsuario.get("Nombre") != null) {
                    username = responseUsuario.get("Nombre") as String
                }
                if(responseUsuario.get("ApPaterno") != null) {
                    username += " " + responseUsuario.get("ApPaterno") as String
                }
                txtUsername?.text = username
                otherPhoto = if(responseUsuario.get("Foto") != null)      responseUsuario.get("Foto") as String else ""

                db.collection("Conversacion").document(idChat!!).collection("Mensajes")
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
                            if (msgAux.Autor == documentReferenceUserLogged) {
                                msgAux.FotoPerfil = if(myPhoto != null)    myPhoto as String else ""
                            } else {
                                msgAux.FotoPerfil = if(otherPhoto != null)    otherPhoto as String else ""
                            }

                            msgParam.add(msgAux)
                            chatAdapter.addItem(msgAux)

                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error consiguiendo los Mensajes", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error consiguiendo la información del Usuario", exception)
            }
    }

    private fun enviarMensaje() {
        var mensaje : String = txtMsg?.text.toString()
        if (mensaje.isEmpty()) {
            txtMsg?.setError("Mensaje vacío")

        } else {
            txtMsg?.setText("")

            db.collection("Conversacion").document(idChat!!).collection("Mensajes")
                .add(
                    hashMapOf(
                        "Texto" to mensaje,
                        "Autor" to documentReferenceUserLogged,
                        "FechaCreacion" to FieldValue.serverTimestamp()
                    )
                )

        }

    }

}