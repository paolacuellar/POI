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
                    setUpChat()
                }

            }

    }

    private fun setUpChat() {
        var documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(
            DataManager.emailUsuario!!)

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
                            var idMsgAux = ""
                            idMsgAux = responseMsg.id

                            db.collection("Conversacion").document(idChat!!)
                                .collection("Mensajes").document(idMsgAux)
                                .get()
                                .addOnSuccessListener { responseContent ->

                                    var msgAux = MensajesModel()
                                    msgAux.id = responseContent.id
                                    msgAux.Texto = if(responseContent.get("Texto") != null)    responseContent.get("Texto") as String else ""
                                    msgAux.Autor = responseContent.get("Autor") as DocumentReference
                                    msgAux.Foto = if(responseContent.get("Foto") != null)    responseContent.get("Foto") as String else ""
                                    msgAux.NombreDocumento = if(responseContent.get("NombreDocumento") != null)    responseContent.get("NombreDocumento") as String else ""
                                    msgAux.Latitud = if(responseContent.get("Latitud") != null)    responseContent.get("Latitud") as String else ""
                                    msgAux.Longitud = if(responseContent.get("Longitud") != null)    responseContent.get("Longitud") as String else ""
                                    msgAux.FechaCreacion = responseContent.get("FechaCreacion") as Timestamp
                                    if (msgAux.Autor == documentReferenceUserLogged) {
                                        msgAux.FotoPerfil = if(myPhoto != null)    myPhoto as String else ""
                                    } else {
                                        msgAux.FotoPerfil = if(otherPhoto != null)    otherPhoto as String else ""
                                    }

                                    msgParam.add(msgAux)
                                    chatAdapter.addItem(msgAux)

                                }
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
        var documentReferenceUserLogged = FirebaseFirestore.getInstance().collection("Usuarios").document(
            DataManager.emailUsuario!!)

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