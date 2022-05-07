package com.example.purrrfectpoi.fragments

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrrfectpoi.MapsActivity
import com.example.purrrfectpoi.Models.MensajesModel
import com.example.purrrfectpoi.R
import com.example.purrrfectpoi.adapters.GroupChatAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.psm.hiring.Utils.DataManager
import java.util.*

class GroupChatFragment:Fragment() {

    private lateinit var recyclerViewMsgs : RecyclerView
    private lateinit var groupChatAdapter : GroupChatAdapter

    var btnEnviarMsg : ImageView? = null;
    var btnEnviarImg : Button? = null;
    var btnEnviarDoc : Button? = null;
    var btnEnviarLoc : Button? = null;
    var txtMsg : EditText? = null;

    var idGrupo : String? = null;
    var idGroupChat : String? = null;

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
        this.btnEnviarImg = getView()?.findViewById<Button>(R.id.sendImageButton)
        this.btnEnviarDoc = getView()?.findViewById<Button>(R.id.sendDocumentButton)
        this.btnEnviarLoc = getView()?.findViewById<Button>(R.id.sendLocationButton)
        this.txtMsg = getView()?.findViewById<EditText>(R.id.messageTextField)

        this.recyclerViewMsgs = view.findViewById<RecyclerView>(R.id.messagesRecyclerView)

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

        idGrupo = arguments?.getString("grupoId")

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

                                        groupChatAdapter.addItem(msgAux)
                                    }
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
        groupChatAdapter.setOnItemClickListener(object : GroupChatAdapter.onItemClickListener{
            override fun onMapClick(view: View, position: Int) {
                val intent = Intent(activity, MapsActivity::class.java)
                intent.putExtra("Latitud", msgParam[position].Latitud)
                intent.putExtra("Longitud", msgParam[position].Longitud)
                startActivity(intent)
            }

            override fun onDocumentClick(view: View, position: Int) {
                val manager = activity?.applicationContext?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                FirebaseStorage.getInstance().getReference("files/Mensajes/${msgParam[position].NombreDocumento}").downloadUrl
                    .addOnSuccessListener {
                        val request = DownloadManager.Request(it)
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, msgParam[position].NombreDocumento)
                        manager.enqueue(request)
                        Toast.makeText(activity, "Archivo descargado", Toast.LENGTH_SHORT).show()
                    }
            }
        })

        db = FirebaseFirestore.getInstance()
        db.collection("Grupos").document(idGrupo!!)
            .get()
            .addOnSuccessListener { responseGrupo ->

                if (responseGrupo.get("Conversacion") != null) {
                    var idChatGrupo = responseGrupo.get("Conversacion") as DocumentReference
                    idGroupChat = idChatGrupo.id
                }

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
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
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
                Toast.makeText(activity, "Se requiere aceptar el permiso", Toast.LENGTH_SHORT).show()
                enviarUbicacion()
            }
            else {
                Toast.makeText(activity, "Permiso concedido", Toast.LENGTH_SHORT).show()
                abrirMapa()
            }
        }
    }

    private fun abrirMapa() {
        startActivityForResult(Intent(activity, MapsActivity::class.java), 222)
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
                    db.collection("Conversacion").document(idGroupChat!!).collection("Mensajes")
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
            db.collection("Conversacion").document(idGroupChat!!).collection("Mensajes")
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
            var index = data.resolveType(requireActivity().applicationContext)?.indexOf("/")
            var ext = data.resolveType(requireActivity().applicationContext)?.drop(index!! + 1)
            var strFile = UUID.randomUUID().toString() + "." + ext
            var pathFile = "files/Mensajes/${strFile}"
            var fileRef = FirebaseStorage.getInstance().reference.child(pathFile)
            fileRef.putFile(filepath)
                .addOnSuccessListener { responseFileUpload ->

                    db = FirebaseFirestore.getInstance()
                    db.collection("Conversacion").document(idGroupChat!!).collection("Mensajes")
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