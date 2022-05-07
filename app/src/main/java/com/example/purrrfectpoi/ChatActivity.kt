package com.example.purrrfectpoi

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
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
                    for (doc in snapshot) {
                        if (doc.get("FechaCreacion") != null) {
                            var msgAux = MensajesModel()
                            msgAux.id = doc.id
                            msgAux.Texto = if (doc.get("Texto") != null)    doc.get("Texto") as String else ""
                            msgAux.Autor = doc.get("Autor") as DocumentReference
                            msgAux.Foto = if(doc.get("Foto") != null)    doc.get("Foto") as String else ""
                            msgAux.NombreDocumento = if(doc.get("NombreDocumento") != null)    doc.get("NombreDocumento") as String else ""
                            msgAux.Latitud = if(doc.get("Latitud") != null)    doc.get("Latitud") as String else ""
                            msgAux.Longitud = if(doc.get("Longitud") != null)    doc.get("Longitud") as String else ""
                            msgAux.FechaCreacion = doc.get("FechaCreacion") as Timestamp

                            chatAdapter.addItem(msgAux)
                        }
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
            override fun onMapClick(view: View, position: Int) {
                val intent = Intent(this@ChatActivity, MapsActivity::class.java)
                intent.putExtra("Latitud", msgParam[position].Latitud)
                intent.putExtra("Longitud", msgParam[position].Longitud)
                startActivity(intent)
            }
            override fun onDocumentClick(view: View, position: Int) {
                val manager = applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                FirebaseStorage.getInstance().getReference("files/Mensajes/${msgParam[position].NombreDocumento}").downloadUrl
                    .addOnSuccessListener {
                        val request = DownloadManager.Request(it)
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, msgParam[position].NombreDocumento)
                        manager.enqueue(request)
                        Toast.makeText(this@ChatActivity, "Archivo descargado", Toast.LENGTH_SHORT).show()
                    }
            }
        })

        db = FirebaseFirestore.getInstance()
        db.collection("Usuarios").document(chatTo!!).get()
            .addOnSuccessListener { responseUsuario ->

                var username : String = ""
                if (responseUsuario.get("Nombre") != null) {
                    username = responseUsuario.get("Nombre") as String + " "
                }
                if (responseUsuario.get("ApPaterno") != null) {
                    username += responseUsuario.get("ApPaterno") as String
                }
                txtUsername?.text = username

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
        var i = Intent ()
        i.setType("*/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose File"), 333)
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
        }
        else if (requestCode == 333 && resultCode == Activity.RESULT_OK && data != null) {
            var filepath = data.data!!
            var index = data.resolveType(applicationContext)?.indexOf("/")
            var ext = data.resolveType(applicationContext)?.drop(index!! + 1)
            var strFile = UUID.randomUUID().toString() + "." + ext
            var pathFile = "files/Mensajes/${strFile}"
            var fileRef = FirebaseStorage.getInstance().reference.child(pathFile)
            fileRef.putFile(filepath)
                .addOnSuccessListener { responseFileUpload ->

                    db = FirebaseFirestore.getInstance()
                    db.collection("Conversacion").document(idChat!!).collection("Mensajes")
                        .add(
                            hashMapOf(
                                "NombreDocumento" to strFile,
                                "Autor" to documentReferenceUserLogged,
                                "FechaCreacion" to FieldValue.serverTimestamp()
                            )
                        )

                }
        }

    }

}