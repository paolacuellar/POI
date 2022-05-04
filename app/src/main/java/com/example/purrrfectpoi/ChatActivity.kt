package com.example.purrrfectpoi

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.Models.MensajesModel
import com.example.purrrfectpoi.adapters.ChatAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerViewMsgs : RecyclerView
    private lateinit var chatAdapter : ChatAdapter

    var btnRegresar : ImageView? =  null;
    var txtUsername : TextView? = null;
    var btnEnviarMsg : ImageView? = null;
    var btnEnviarImg : Button? = null;
    var btnEnviarDoc : Button? = null;
    var btnEnviarLoc : Button? = null;
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

        this.btnRegresar = findViewById<ImageView>(R.id.chat_header_back)
        this.txtUsername = findViewById<TextView>(R.id.chat_txt_nombre_user)
        this.btnEnviarMsg = findViewById<ImageView>(R.id.sendMessageButton)
        this.btnEnviarImg = findViewById<Button>(R.id.sendImageButton)
        this.btnEnviarDoc = findViewById<Button>(R.id.sendDocumentButton)
        this.btnEnviarLoc = findViewById<Button>(R.id.sendLocationButton)
        this.txtMsg = findViewById<EditText>(R.id.messageTextField)

        this.btnRegresar?.setOnClickListener {
            onBackPressed()
        }
        txtUsername?.text = chatTo
        this.btnEnviarMsg?.setOnClickListener {
            enviarMensaje()
        }
        this.btnEnviarImg?.setOnClickListener {
            enviarImagen()
        }
        this.btnEnviarDoc?.setOnClickListener {
            enviarDocumento()
        }
        this.btnEnviarLoc?.setOnClickListener {
            enviarUbicacion()
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
        var msgParam : MutableList<MensajesModel> = mutableListOf()
        chatAdapter = ChatAdapter(msgParam)
        recyclerViewMsgs.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(context)
        }
        chatAdapter.setOnItemClickListener(object : ChatAdapter.onItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@ChatActivity, MapsActivity::class.java)
                intent.putExtra("Latitud", msgParam[position].Latitud)
                intent.putExtra("Longitud", msgParam[position].Longitud)
                startActivity(intent)
            }
        })

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

                            //msgParam.add(msgAux)
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

            db = FirebaseFirestore.getInstance()
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

    private fun enviarImagen() {
        var i = Intent ()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose Picture"), 111)
    }

    private fun enviarDocumento() {

    }

    private fun enviarUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
        else {
            abrirMapa()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Se requiere aceptar el permiso", Toast.LENGTH_SHORT).show()
                enviarUbicacion()
            }
            else {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
                abrirMapa()
            }
        }
    }

    private fun abrirMapa() {
        startActivityForResult(Intent(this, MapsActivity::class.java), 222)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            var filepath = data.data!!
            var strImage = UUID.randomUUID().toString() + ".jpg"
            var pathImage = "images/Mensajes/${strImage}"
            var imageRef = FirebaseStorage.getInstance().reference.child(pathImage)
            imageRef.putFile(filepath)
                .addOnSuccessListener { responseImageUpload ->

                    db = FirebaseFirestore.getInstance()
                    db.collection("Conversacion").document(idChat!!).collection("Mensajes")
                        .add(
                            hashMapOf(
                                "Foto" to strImage,
                                "Autor" to documentReferenceUserLogged,
                                "FechaCreacion" to FieldValue.serverTimestamp()
                            )
                        )

                }
        }
        else if (requestCode == 222 && resultCode == Activity.RESULT_OK && data != null) {
            var lat = data.getStringExtra("Latitud")
            var lon = data.getStringExtra("Longitud")

            db = FirebaseFirestore.getInstance()
            db.collection("Conversacion").document(idChat!!).collection("Mensajes")
                .add(
                    hashMapOf(
                        "Latitud" to lat,
                        "Longitud" to lon,
                        "Autor" to documentReferenceUserLogged,
                        "FechaCreacion" to FieldValue.serverTimestamp()
                    )
                )
            //Toast.makeText(this, "Latitud: " + lat + ", Longitud: " + lon, Toast.LENGTH_SHORT).show()
        }

    }

}